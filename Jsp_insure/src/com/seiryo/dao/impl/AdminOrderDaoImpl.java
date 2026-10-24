package com.seiryo.dao.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.seiryo.dao.AdminOrderDao;
import com.seiryo.entity.My_order_info;
import com.seiryo.utile.JDBCUTILE;

/**
 * 订单管理 Dao 实现（后台）
 * 为什么用 LEFT JOIN 而不是普通 JOIN？
 *   JOIN（INNER JOIN）= 两边都有数据才显示。万一商品被下架删掉了，
 *   这笔订单就会凭空消失，订单金额对不上账！
 *   LEFT JOIN = 以左边（订单）为准，右边的商品名查不到就显示空，但订单还在。
 */
public class AdminOrderDaoImpl implements AdminOrderDao {

    /**
     * 1、订单一览表
     * 业务逻辑层（拼装参数时）：用list集合，List.add()可以随便加；数据长度不能变，一开始就得算好条件
     * 底层执行层（传给数据库时）：底层数据库驱动工具为了严谨，不认list，只认固定长度的数组
     */
    public List<My_order_info> selectOrderList(String orderId, String beneficiaryName, String phone) {

  
        // 第 1 步：拼 SQL
        // 固定写法 "WHERE 1 = 1"：它永远成立，所以后面每个条件都可以无脑加 " AND xxx"，
        // 不用判断「我到底是不是第一个条件」，方便动态拼接其他的查询条件。不然下面需要进一步用if-else判断条件
  
        String sql = "SELECT o.order_id, o.user_id, o.commodity_id, "
                   + "       oi.order_info_id, oi.order_sum_price, "
                   + "       oi.order_beneficiary_name, oi.order_beneficiary_city, oi.order_beneficiary_phone, "
                   + "       oi.order_commodity_time_start, oi.order_commodity_time_end, oi.order_commodity_time_length, "
                   + "       oi.order_commodity_liability1, oi.order_commodity_liability2, oi.order_commodity_liability3, "
                   + "       oi.order_commodity_liability4, oi.order_commodity_liability5, oi.order_commodity_liability6, "
                   + "       oi.order_commodity_liability7, "
                   + "       c.commodity_name, u.user_name "
                   + "FROM my_order o "
                   + "LEFT JOIN my_order_info oi ON o.order_id    = oi.order_id "
                   + "LEFT JOIN commodity     c  ON o.commodity_id = c.commodity_id "
                   + "LEFT JOIN my_user_info  u  ON o.user_id      = u.user_id "
                   + "WHERE 1 = 1 ";

     
        // 第 2 步：准备参数
        // List<Object> params 是 Object 类型，因为订单号是字符串，什么都能装。
        // add 的顺序必须和 SQL 里 ? 出现的顺序一模一样

        List<Object> params = new ArrayList<Object>();

        // LIKE + 百分号 % 就是「模糊查询」：%张三% 表示只要包含「张三」就算命中
        if (orderId != null && !orderId.trim().equals("")) {
            sql = sql + " AND o.order_id LIKE ? ";
            params.add("%" + orderId.trim() + "%");
        }
        if (beneficiaryName != null && !beneficiaryName.trim().equals("")) {
            sql = sql + " AND oi.order_beneficiary_name LIKE ? ";
            params.add("%" + beneficiaryName.trim() + "%");
        }
        if (phone != null && !phone.trim().equals("")) {
            sql = sql + " AND oi.order_beneficiary_phone LIKE ? ";
            params.add("%" + phone.trim() + "%");
        }

        // 第 3 步：排序
        // order_id 是 yyyyMMddHHmmss（比如 20261018015358），
        // 这种格式下「字符串比较」和「时间比较」结果完全一样，
        // 所以 DESC（倒序）就是「最新订单排最前面」，不需要转类型。
        // （对比用户管理一览表中：user_money 是纯数字金额的字符串，那个必须写 + 0 才能正确排序）

        sql = sql + " ORDER BY o.order_id DESC";
        
        // 第 4 步：执行查询，把结果一行一行装进 List（把查出来的“表格数据”转换成java里的“对象集合”

        List<My_order_info> list = new ArrayList<My_order_info>();
        //数据库返回的“结果集”——查询结果表
        ResultSet rs = null;
        try {
            // params.toArray()：把 List 变成数组，因为 JDBCUTILE 用的是可变参数
            rs = JDBCUTILE.selectDataBaseTable(sql, params.toArray());

            // rs.next() = 把游标往下挪一行，挪到了就返回 true
            // while 循环 = 有多少行就跑多少次
            while (rs.next()) {
                // 前三个字段来自父表 My_order，用有参构造方法传进去，为了应对left join带来的null风险
            	//直接根据列名去当前行数据里取值
                My_order_info o = new My_order_info(
                        rs.getString("order_id"),
                        rs.getInt("user_id"),
                        rs.getInt("commodity_id"));

                // 其余字段来自关联表，用set方法塞进去
                o.setOrder_info_id(rs.getInt("order_info_id"));
                o.setOrder_sum_price(rs.getString("order_sum_price"));
                o.setOrder_beneficiary_name(rs.getString("order_beneficiary_name"));
                o.setOrder_beneficiary_city(rs.getString("order_beneficiary_city"));
                o.setOrder_beneficiary_phone(rs.getString("order_beneficiary_phone"));
                o.setOrder_commodity_time_start(rs.getString("order_commodity_time_start"));
                o.setOrder_commodity_time_end(rs.getString("order_commodity_time_end"));
                o.setOrder_commodity_time_length(rs.getString("order_commodity_time_length"));
                o.setOrder_commodity_liability1(rs.getString("order_commodity_liability1"));
                o.setOrder_commodity_liability2(rs.getString("order_commodity_liability2"));
                o.setOrder_commodity_liability3(rs.getString("order_commodity_liability3"));
                o.setOrder_commodity_liability4(rs.getString("order_commodity_liability4"));
                o.setOrder_commodity_liability5(rs.getString("order_commodity_liability5"));
                o.setOrder_commodity_liability6(rs.getString("order_commodity_liability6"));
                o.setOrder_commodity_liability7(rs.getString("order_commodity_liability7"));

                // 这两个是 JOIN 出来的，实体里专门留了字段来接
                o.setCommodity_name(rs.getString("commodity_name"));
                o.setUser_name(rs.getString("user_name"));

                list.add(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                JDBCUTILE.dbClose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 2、删除订单：先删明细表，再删主表
     * 和你 AdminUserDaoImpl.deleteUser（先删 my_user_info 再删 my_user）完全同一个套路
     */
    public boolean deleteOrder(String orderId) {

        String sql1 = "DELETE FROM my_order_info WHERE order_id = ?";
        String sql2 = "DELETE FROM my_order      WHERE order_id = ?";
        try {
            boolean r1 = JDBCUTILE.upDataBaseTable(sql1, orderId);
            if (!r1) {
            	// 明细没删掉就停手，别把主表也删了
                return false;  
            }
            return JDBCUTILE.upDataBaseTable(sql2, orderId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                JDBCUTILE.dbClose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 3、总订单数
     * COUNT(*) 是 SQL 的计数函数，AS cnt 给结果起个名字，后面 rs.getInt("cnt") 才能取到
     */
    public int countOrder() {

        String sql = "SELECT COUNT(*) AS cnt FROM my_order";
        ResultSet rs = null;
        try {
            rs = JDBCUTILE.selectDataBaseTable(sql);
            // 只有一行结果，所以用 if 就够（多行才用 while）
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                JDBCUTILE.dbClose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 4、销售总额
     * CAST(order_sum_price AS DECIMAL(14,2))：
     * order_sum_price 在数据库里是 VARCHAR（文字），用 CAST 转成小数
     * IFNULL(...,0)：一张订单都没有时返回 0，不然页面会显示空白。
     * 变小数、计算总和、null变成0
     */
    public String sumOrderPrice() {

        String sql = "SELECT IFNULL(SUM(CAST(order_sum_price AS DECIMAL(14,2))),0) AS total "
                   + "FROM my_order_info";
        ResultSet rs = null;
        try {
            rs = JDBCUTILE.selectDataBaseTable(sql);
            if (rs.next()) {
                return rs.getString("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                JDBCUTILE.dbClose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "0";
    }
}
