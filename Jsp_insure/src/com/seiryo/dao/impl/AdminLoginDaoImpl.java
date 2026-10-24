package com.seiryo.dao.impl;

import java.sql.ResultSet;

import com.seiryo.dao.AdminLoginDao;
import com.seiryo.entity.AdminUser;
import com.seiryo.utile.JDBCUTILE;

public class AdminLoginDaoImpl implements AdminLoginDao {
	
/**
 * 1、根据账号查找管理员
 */
 public AdminUser selectByAccount(String account) {
	 String sql="SELECT admin_id,admin_account,admin_password,admin_name,admin_role,admin_status,created_at FROM "
	 		+ "admin_user WHERE admin_account=? ";
	 ResultSet rs=null;
	 
	 try {
		rs=JDBCUTILE.selectDataBaseTable(sql,account);
		if(rs.next()) {
			//把查到的这一行，装成AdminUser对象返回
			AdminUser admin=new AdminUser();
			admin.setAdmin_id(rs.getInt("admin_id"));
			admin.setAdmin_account(rs.getString("admin_account"));
			admin.setAdmin_password(rs.getString("admin_password"));
			admin.setAdmin_name(rs.getString("admin_name"));
			admin.setAdmin_role(rs.getInt("admin_role")); 
			admin.setAdmin_status(rs.getInt("admin_status"));
			admin.setCreated_at(rs.getString("created_at"));
			return admin;
		}
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		try {
			JDBCUTILE.dbClose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 return null;
 }
 /**
  * 2、根据账号查找管理员:看注册时候有没有账号重复了
  */
 public int countByAccount(String account) {
	 //看我传来的参数有几行，保存数量，数量命名为cnt;返回为0代表不重复
	 String sql="SELECT COUNT(*) AS cnt FROM admin_user WHERE admin_account=? ";
	 ResultSet rs=null;
	 try {
		rs=JDBCUTILE.selectDataBaseTable(sql, account);
		if(rs.next()) {
			//返还数量
			return rs.getInt("cnt");
		}
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		try {
			JDBCUTILE.dbClose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 return 0;
 }
 
 /**
  * 注册：插入一条管理员信息数据
  * 这里是「新人在注册页自己注册」用的，一律注册成「一般管理者」(role=2)。
  * 总管理员在后台给别人开账号，走的是 AdminAccountDaoImpl.insertAccount，
  * 那边可以选 1 或 2
  */
public boolean insertAccount(AdminUser admin) {
	
	String sql="INSERT INTO admin_user (admin_account,admin_password,admin_name,admin_status,admin_role)VALUES(?, ?, ?, ? ,? )";
	boolean bo = false;
	try {
		bo=JDBCUTILE.upDataBaseTable(sql, admin.getAdmin_account(),admin.getAdmin_password(),admin.getAdmin_name(),admin.getAdmin_status(),admin.getAdmin_role());
	
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		try {
			JDBCUTILE.dbClose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//返回执行结果
	return bo;
}
 
 
 
 
}
