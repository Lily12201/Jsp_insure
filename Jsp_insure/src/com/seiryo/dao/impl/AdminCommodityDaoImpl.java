package com.seiryo.dao.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.seiryo.dao.AdminCommodityDao;
import com.seiryo.entity.AdminCommodity;
import com.seiryo.utile.JDBCUTILE;

/**
 * 商品管理：commodity中加了商品状态： commodity_status
 * @ClassName: AdminCommodityDaoImpl  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 李 
 * @date 2026年10月22日  
 *
 */
public class AdminCommodityDaoImpl implements AdminCommodityDao {
/**
 * 1、商品列表：ID/商品名/查全部
 */
    public List<AdminCommodity> selectCommodityList(String keyword, String type) {

        // 子查询顺手把「这个商品卖了多少单」查出来，列表页能显示销量
        String sql = "SELECT c.*, "
                   + " (SELECT COUNT(*) FROM my_order o WHERE o.commodity_id = c.commodity_id) AS order_count "
                   + "FROM commodity c WHERE 1 = 1 ";

        List<Object> params = new ArrayList<Object>();

        if (keyword != null && !keyword.trim().equals("")) {
            String kw = keyword.trim();

            //   试着把输入的内容转成数字：
            //   转成功了说明输的是纯数字（比如 12），当成商品ID查
            //   转失败了会抛异常跳到 catch，说明输的是文字，按商品名查
            try {
                int id = Integer.parseInt(kw);

                // 用 OR 是因为：商品名里也可能带数字（比如「保険123」），两种都要查到
                sql = sql + " AND (c.commodity_id = ? OR c.commodity_name LIKE ?) ";
                params.add(id);
                params.add("%" + kw + "%");

            } catch (NumberFormatException e) {
                // 不是数字，只按商品名模糊查
                sql = sql + " AND c.commodity_name LIKE ? ";
                params.add("%" + kw + "%");
            }
        }
        if (type != null && !type.trim().equals("")) {
            sql = sql + " AND c.commodity_type = ? ";
            params.add(type.trim());
        }
        sql = sql + " ORDER BY c.commodity_id DESC";

        List<AdminCommodity> list = new ArrayList<AdminCommodity>();
        ResultSet rs = null;
        try {
            rs = JDBCUTILE.selectDataBaseTable(sql, params.toArray());
            while (rs.next()) {
                list.add(pack(rs));
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
        return list;
    }
/**
 * 2、根据 id 查一个商品
 */
    public AdminCommodity selectCommodityById(int commodityId) {

        String sql = "SELECT c.*, "
                   + " (SELECT COUNT(*) FROM my_order o WHERE o.commodity_id = c.commodity_id) AS order_count "
                   + "FROM commodity c WHERE c.commodity_id = ? ";

        ResultSet rs = null;
        try {
            rs = JDBCUTILE.selectDataBaseTable(sql, commodityId);
            if (rs.next()) {
                return pack(rs);
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
        return null;
    }
/**
 * 3、新增商品
 */
    public boolean insertCommodity(AdminCommodity c) {

        String sql = "INSERT INTO commodity "
                   + "(commodity_name, commodity_price, commodity_introduce, commodity_img, "
                   + " commodity_type, commodity_time_length, commodity_age_start, commodity_age_end, "
                   + " commodity_status) "
                   + "VALUES (?,?,?,?,?,?,?,?,1)";

        try {
            return JDBCUTILE.upDataBaseTable(sql,
                    c.getCommodity_name(),
                    c.getCommodity_price(),
                    c.getCommodity_introduce(),
                    c.getCommodity_img(),
                    c.getCommodity_type(),
                    c.getCommodity_time_length(),
                    c.getCommodity_age_start(),
                    c.getCommodity_age_end());
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
 * 4、修改商品
 */
    public boolean updateCommodity(AdminCommodity c) {

        String sql = "UPDATE commodity SET "
                   + " commodity_name = ?, commodity_price = ?, commodity_introduce = ?, "
                   + " commodity_img = ?, commodity_type = ?, commodity_time_length = ?, "
                   + " commodity_age_start = ?, commodity_age_end = ? "
                   + "WHERE commodity_id = ?";

        try {
            return JDBCUTILE.upDataBaseTable(sql,
                    c.getCommodity_name(),
                    c.getCommodity_price(),
                    c.getCommodity_introduce(),
                    c.getCommodity_img(),
                    c.getCommodity_type(),
                    c.getCommodity_time_length(),
                    c.getCommodity_age_start(),
                    c.getCommodity_age_end(),
                    c.getCommodity_id());
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
 * 5、上架 / 下架
 */
    public boolean updateCommodityStatus(int commodityId, int status) {

        String sql = "UPDATE commodity SET commodity_status = ? WHERE commodity_id = ?";
        try {
            return JDBCUTILE.upDataBaseTable(sql, status, commodityId);
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
 * 6、删除商品
 */
    public boolean deleteCommodity(int commodityId) {

        String sql = "DELETE FROM commodity WHERE commodity_id = ?";
        try {
            return JDBCUTILE.upDataBaseTable(sql, commodityId);
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
 * 7、查这个商品有没有被买过（买过就不让删，只能下架）
 */
    public int countOrderByCommodityId(int commodityId) {

        String sql = "SELECT COUNT(*) AS cnt FROM my_order WHERE commodity_id = ?";
        ResultSet rs = null;
        try {
            rs = JDBCUTILE.selectDataBaseTable(sql, commodityId);
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
 * 把 ResultSet 的一行装进 AdminCommodity
 * （每个查询都要装一遍，抽成一个方法省得重复写十几行）
 */
    private AdminCommodity pack(ResultSet rs) throws Exception {
        AdminCommodity c = new AdminCommodity();
        c.setCommodity_id(rs.getInt("commodity_id"));
        c.setCommodity_name(rs.getString("commodity_name"));
        c.setCommodity_price(rs.getString("commodity_price"));
        c.setCommodity_introduce(rs.getString("commodity_introduce"));
        c.setCommodity_img(rs.getString("commodity_img"));
        c.setCommodity_age_start(rs.getInt("commodity_age_start"));
        c.setCommodity_age_end(rs.getInt("commodity_age_end"));
        c.setCommodity_time_length(rs.getString("commodity_time_length"));
        c.setCommodity_applicable_gender(rs.getInt("commodity_applicable_gender"));
        c.setCommodity_applicable_location(rs.getString("commodity_applicable_location"));
        c.setCommodity_type(rs.getString("commodity_type"));
        c.setCommodity_status(rs.getInt("commodity_status"));
        c.setOrder_count(rs.getInt("order_count"));
        return c;
    }
}
