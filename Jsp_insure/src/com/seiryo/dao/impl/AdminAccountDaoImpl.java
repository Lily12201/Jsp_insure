package com.seiryo.dao.impl;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

import com.seiryo.dao.AdminAccountDao;
import com.seiryo.entity.AdminUser;
import com.seiryo.utile.JDBCUTILE;


public class AdminAccountDaoImpl implements AdminAccountDao {

/**
 * 1、查全部管理员（按 id 倒序，新加的排最上面）：总管理员和普通管理员看的查询列表不一样
 */
	 public List<AdminUser> selectAccountList(int myRole, int myId) {

	        String sql = "SELECT admin_id, admin_account, admin_password, admin_name, "
	                   + "admin_role, admin_status, created_at "
	                   + "FROM admin_user ";

	        // 总管理员看全部；普通管理员只能看见自己那一行
	        //造一个「可以一直往里塞东西的兜子」，用来装 SQL 里 ? 要填的值
	        List<Object> params = new ArrayList<Object>();
	        //分支 → 只看自己 → 权限变小。这正是「取不到就按最小的给」的原则，
	        if (myRole != 1) {
	        	// 第 1 个值 → 填进第 1 个问号
	            sql = sql + "WHERE admin_id = ? ";
	            params.add(myId);
	        }
	        sql = sql + "ORDER BY admin_id DESC";

	        List<AdminUser> list = new ArrayList<AdminUser>();
	        ResultSet rs = null;
	        try {
	            rs = JDBCUTILE.selectDataBaseTable(sql, params.toArray());
	            while (rs.next()) {
	            	//把查询的代码放到了小工具pack中了，方便调用
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
  * 2、查某一个管理员（用来判断对方是不是总管理员、验证旧密码对不对）
  */
	 public AdminUser selectById(int adminId) {

	        String sql = "SELECT admin_id, admin_account, admin_password, admin_name, "
	                   + "admin_role, admin_status, created_at "
	                   + "FROM admin_user WHERE admin_id = ?";

	        ResultSet rs = null;
	        try {
	            rs = JDBCUTILE.selectDataBaseTable(sql, adminId);
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
 * 3、查现在有几个「启用状态的总管理员」
 */
	    public int countSuperAdmin() {

	        String sql = "SELECT COUNT(*) AS cnt FROM admin_user "
	                   + "WHERE admin_role = 1 AND admin_status = 1";
	        ResultSet rs = null;
	        try {
	            rs = JDBCUTILE.selectDataBaseTable(sql);
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
 * 4、查账号有没有被占用。返回 0 = 没被占用
 */
    public int countByAccount(String account) {
    	//看我传来的参数有几行，保存数量，数量命名为cnt;返回为0代表不重复
        String sql = "SELECT COUNT(*) AS cnt FROM admin_user WHERE admin_account = ?";
        ResultSet rs = null;
        try {
            rs = JDBCUTILE.selectDataBaseTable(sql, account);
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
 * 5、新增一个管理员
 */
    public boolean insertAccount(AdminUser admin) {
        String sql = "INSERT INTO admin_user "
                   + "(admin_account, admin_password, admin_name, admin_status) "
                   + "VALUES (?, ?, ?, ?)";
        try {
            return JDBCUTILE.upDataBaseTable(sql,
                    admin.getAdmin_account(),
                    admin.getAdmin_password(),
                    admin.getAdmin_name(),
                    admin.getAdmin_status());
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
 * 6、更新管理员的状态
 */
    public boolean updateStatus(int adminId, int status) {
        String sql = "UPDATE admin_user SET admin_status = ? WHERE admin_id = ?";
        try {
            return JDBCUTILE.upDataBaseTable(sql, status, adminId);
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
 * 7、更新管理员的密码
 */
    public boolean updatePassword(int adminId, String password) {
        String sql = "UPDATE admin_user SET admin_password = ? WHERE admin_id = ?";
        try {
            return JDBCUTILE.upDataBaseTable(sql, password, adminId);
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
  * 8、更新管理员的身份角色：总管理还是普通管理员   
  */
    public boolean updateRole(int adminId, int role) {
        String sql = "UPDATE admin_user SET admin_role = ? WHERE admin_id = ?";
        try {
            return JDBCUTILE.upDataBaseTable(sql, role, adminId);
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
 * 9、删除管理员账号
 */
    public boolean deleteAccount(int adminId) {
        String sql = "DELETE FROM admin_user WHERE admin_id = ?";
        try {
            return JDBCUTILE.upDataBaseTable(sql, adminId);
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
 * 10、把 ResultSet 的一行装进 AdminUser
 * （每个查询都要装一遍，抽成一个方法省得重复写）
 */
    private AdminUser pack(ResultSet rs) throws Exception {
        AdminUser a = new AdminUser();
        a.setAdmin_id(rs.getInt("admin_id"));
        a.setAdmin_account(rs.getString("admin_account"));
        a.setAdmin_password(rs.getString("admin_password"));
        a.setAdmin_name(rs.getString("admin_name"));
        a.setAdmin_role(rs.getInt("admin_role"));
        a.setAdmin_status(rs.getInt("admin_status"));
        a.setCreated_at(rs.getString("created_at"));
        return a;
    }
   
}
