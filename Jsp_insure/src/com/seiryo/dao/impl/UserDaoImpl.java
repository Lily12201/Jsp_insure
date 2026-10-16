package com.seiryo.dao.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.seiryo.dao.UserDao;
import com.seiryo.entity.MyUserInfo;
import com.seiryo.utile.JDBCUTILE;

/**
 * 
 * @ClassName: UserDaoImpl  
 * @Description: TODO(用户数据层实现类)  
 * @author 10748  
 * @date 2025年10月14日
 */
public class UserDaoImpl implements UserDao{

	/**
	 * 用户注册
	 */
	@Override
	public boolean insertUser(MyUserInfo myUserInfo) {
		String sql1 = "INSERT INTO my_user(user_email,user_password) VALUES(?,?)";
		String sql2 = "INSERT INTO my_user_info(user_id,user_phone,user_money,user_name,user_birthday,user_sex,user_status) VALUES(?,?,?,?,?,?,?)";
		boolean bo = false;
		try {
			bo = JDBCUTILE.upDataBaseTable(sql1, myUserInfo.getUser_email(),myUserInfo.getUser_password());
			if(bo) {
				Integer user_id = selectUserIdByEmail(myUserInfo.getUser_email());
				bo = JDBCUTILE.upDataBaseTable(sql2,user_id,myUserInfo.getUser_phone(),myUserInfo.getUser_money(),myUserInfo.getUser_name(),myUserInfo.getUser_birthday(),myUserInfo.getUser_sex(),myUserInfo.getUser_status());
			}else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bo;
	}
	
	/**
	 * 
	 * @Title: selectUserIdByEmail  
	 * @Description: TODO(通过·邮箱获取用户ID)  
	 * @param @param user_email
	 * @param @return      
	 * @return Integer      
	 * @throws
	 */
	private Integer selectUserIdByEmail(String user_email){
		String sql = "SELECT user_id FROM my_user WHERE user_email = ?";
		ResultSet rest = null;
		Integer user_id = 0;
		try {
			rest = JDBCUTILE.selectDataBaseTable(sql, user_email);
			while(rest.next()) {
				user_id = rest.getInt("user_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user_id;
	}

	/**
	 * 查询所有用户信息
	 */
	@Override
	public List<MyUserInfo> selectAllUserInfo() {
		String sql = "SELECT m1.user_id,m1.user_email,m1.user_password,m2.user_info_id,m2.user_phone,m2.user_money,m2.user_name,m2.user_birthday,m2.user_sex,m2.user_status FROM my_user m1 JOIN my_user_info m2 ON m1.user_id = m2.user_id";
		ResultSet rest = null;
		List<MyUserInfo> list = new ArrayList<MyUserInfo>();
		try {
			rest = JDBCUTILE.selectDataBaseTable(sql);
			while(rest.next()) {
				MyUserInfo myUserInfo = new MyUserInfo(rest.getInt("user_id"), rest.getString("user_email"), rest.getString("user_password"),rest.getInt("user_info_id"), rest.getString("user_phone"), rest.getString("user_money"), rest.getString("user_name"), rest.getString("user_birthday"), rest.getString("user_sex"), rest.getInt("user_status"));
				list.add(myUserInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				JDBCUTILE.dbClose(rest, null, null);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 修改用户信息
	 * 
	 */
	@Override
	public boolean updateUserInfo(Integer user_id, Integer user_info_id, String userPass, String userPhone, String userMoney) {
	    try {
	        String sql1 = "UPDATE my_user SET user_password = ? WHERE user_id = ?";
	        boolean r1 = JDBCUTILE.upDataBaseTable(sql1, userPass, user_id);

	        String sql2 = "UPDATE my_user_info SET user_phone = ?, user_money = ? WHERE user_info_id = ?";
	        boolean r2 = JDBCUTILE.upDataBaseTable(sql2, userPhone, userMoney, user_info_id);

	        return r1 && r2;
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try { JDBCUTILE.dbClose(); 
	        } catch (Exception e){ 
	         e.printStackTrace(); }
	    }
	    return false;
	}



}
