package com.seiryo.dao.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.seiryo.dao.AdminUserDao;
import com.seiryo.entity.MyUserInfo;
import com.seiryo.utile.JDBCUTILE;

/**
 * 用户管理 Dao 实现（后台）
 * @ClassName: AdminUserDaoImpl  
 * @Description: 去查/改你前台已有的 my_user、my_user_info 两张表
 * @author 李 
 * @date 2026年10月22日  
 *
 */
public class AdminUserDaoImpl implements AdminUserDao {
/**
 * 1、查用户列表：键盘为空/数字：ID/文字：姓名
 */
    public List<MyUserInfo> selectUserList(String keyword) {

        // 基础 SQL（和前台 UserDaoImpl一样 ）
        String sql = "SELECT u.user_id, u.user_email, u.user_password, "
                   + "i.user_info_id, i.user_phone, i.user_money, i.user_name, "
                   + "i.user_birthday, i.user_sex, i.user_status "
                   + "FROM my_user u, my_user_info i "
                   + "WHERE u.user_id = i.user_id ";
        
        //创建一个临时集合装数据
        List<Object> params = new ArrayList<Object>();

        if (keyword != null && !keyword.trim().equals("")) {
        	//把去空格后的关键词存到新变量 kw，后面拿 kw去拼 SQL 查询。
            String kw = keyword.trim();

            // 试着把输入的内容转成数字：
            //   转成功了：说明输的是纯数字（比如 37），当成会员ID查
            //   转失败了：会抛异常跳到 catch，说明输的是文字（比如 田中），按姓名查
            try {
                int id = Integer.parseInt(kw);

                // 用 OR 是因为：真有人名字就叫「123」，两种可能都要查到
                sql = sql + " AND (u.user_id = ? OR i.user_name LIKE ?) ";
                params.add(id);
                params.add("%" + kw + "%");

            } catch (NumberFormatException e) {
                // 不是数字，只按姓名模糊查
                sql = sql + " AND i.user_name LIKE ? ";
                params.add("%" + kw + "%");
            }
        }
        //根据用户的余额降序排序
        //重点：使用拼接方法，并且 "+ 0" 是 MySQL 的隐式转换：让字符串当数字参与运算，就变成数字比较了。
        sql = sql + " ORDER BY i.user_money + 0 DESC";

        List<MyUserInfo> list = new ArrayList<MyUserInfo>();
        ResultSet rs = null;
        try {
            rs = JDBCUTILE.selectDataBaseTable(sql, params.toArray());
            while (rs.next()) {
                MyUserInfo user = new MyUserInfo(
                        rs.getInt("user_id"),
                        rs.getString("user_email"),
                        rs.getString("user_password"),
                        rs.getInt("user_info_id"),
                        rs.getString("user_phone"),
                        rs.getString("user_money"),
                        rs.getString("user_name"),
                        rs.getString("user_birthday"),
                        rs.getString("user_sex"),
                        rs.getInt("user_status"));
                list.add(user);
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
 * 2、查询某个用户的余额
 */
    public int selectMoney(int userInfoId) {

        String sql = "SELECT user_money FROM my_user_info WHERE user_info_id = ?";
        ResultSet rs = null;
        try {
            rs = JDBCUTILE.selectDataBaseTable(sql, userInfoId);
            if (rs.next()) {
                String money = rs.getString("user_money");
                // 余额字段是空的，就当 0 元
                if (money == null || money.equals("")) {
                    return 0;
                }
                // 数据库里 user_money 存的是字符串，可能是 "3000" 也可能是 "3000.0"
                // 所以先转成 double 再转 int，直接 Integer.parseInt("3000.0") 会报错
                return (int) Double.parseDouble(money);
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
        // 走到这里说明没查到这个人，返回 -1（和 toInt道理一样：-1 表示"没查到"）
        return -1;
    }

/**
 * 3、改变用户的状态
 */
    public boolean updateUserStatus(int userInfoId, int status) {
        String sql = "UPDATE my_user_info SET user_status = ? WHERE user_info_id = ?";
        try {
            return JDBCUTILE.upDataBaseTable(sql, status, userInfoId);
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
 * 4、给用户充值
 */
    public boolean updateUserMoney(int userInfoId, String money) {
        String sql = "UPDATE my_user_info SET user_money = ? WHERE user_info_id = ?";
        try {
            return JDBCUTILE.upDataBaseTable(sql, money, userInfoId);
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
 * 5、删除用户的信息
 */
    public boolean deleteUser(int userId, int userInfoId) {
        // 先删从表（明细），再删主表。
        String sql1 = "DELETE FROM my_user_info WHERE user_info_id = ?";
        String sql2 = "DELETE FROM my_user     WHERE user_id     = ?";
        try {
            boolean r1 = JDBCUTILE.upDataBaseTable(sql1, userInfoId);
            if (!r1) {
                return false;
            }
            return JDBCUTILE.upDataBaseTable(sql2, userId);
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

}
