package com.seiryo.dao.impl;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

import com.seiryo.dao.OrderDao;
import com.seiryo.entity.MyUserInfo;
import com.seiryo.entity.My_order_info;
import com.seiryo.utile.JDBCUTILE;

public class OrderDaoImpl implements OrderDao{
	/**
	 * 1、按姓名+手机号查用户
	 */
	public MyUserInfo selectUserByNameAndPhone(String name,String phone) {
		String sql="SELECT m1.user_id,m1.user_email,m1.user_password,m2.user_info_id,m2.user_phone,m2.user_money,m2.user_name,m2.user_birthday,m2.user_sex,m2.user_status "
				+ "FROM my_user m1 JOIN my_user_info m2 ON m1.user_id = m2.user_id "
				+ "WHERE m2.user_name=? AND m2.user_phone=? ";
		ResultSet rest = null;
		//我想返回一个用户信息，就不用创建集合接收数据了
		MyUserInfo userInfo=null;
		try {
			rest=JDBCUTILE.selectDataBaseTable(sql, name,phone);
			if(rest.next()) {  
				userInfo = new MyUserInfo(
			
					rest.getInt("user_id"),
					rest.getString("user_email"),
					rest.getString("user_password"),
					rest.getInt("user_info_id"), 
					rest.getString("user_phone"), 
					rest.getString("user_money"), 
					rest.getString("user_name"), 
					rest.getString("user_birthday"), 
					rest.getString("user_sex"), 
					rest.getInt("user_status"));
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
		return userInfo;
	}
	
	public MyUserInfo selectUserByName(String name) {
		String sql = "SELECT m1.user_id,m1.user_email,m1.user_password,m2.user_info_id,m2.user_phone,"
				+ "m2.user_money,m2.user_name,m2.user_birthday,m2.user_sex,m2.user_status "
				+ "FROM my_user m1 JOIN my_user_info m2 ON m1.user_id = m2.user_id "
				+ "WHERE m2.user_name = ?";
		ResultSet rest = null;
		MyUserInfo userInfo = null;
		try {
			rest = JDBCUTILE.selectDataBaseTable(sql, name);
			if(rest.next()) {
				userInfo = new MyUserInfo(
						rest.getInt("user_id"), rest.getString("user_email"),
						rest.getString("user_password"), rest.getInt("user_info_id"),
						rest.getString("user_phone"), rest.getString("user_money"),
						rest.getString("user_name"), rest.getString("user_birthday"),
						rest.getString("user_sex"), rest.getInt("user_status"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { JDBCUTILE.dbClose(); 
		} catch (Exception e) { 
			e.printStackTrace(); }
		}
		return userInfo;
	}	
	/**
	 * 2、插入订单主表
	 */
	public boolean insertMyOrder(String order_id, Integer user_id, Integer commodity_id) {
		String sql = "INSERT INTO my_order(order_id,user_id,commodity_id) VALUES(?,?,?)";
		try {
			return JDBCUTILE.upDataBaseTable(sql, order_id, user_id, commodity_id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { 
				JDBCUTILE.dbClose(); 
			} catch (Exception e) { 
				e.printStackTrace(); }
		}
		return false;
	}
	
	/**
	 * 3、插入订单详情表
	 */
	public boolean insertMyOrderInfo(My_order_info info) {
		String sql = "INSERT INTO my_order_info(order_id,order_commodity_liability1,order_commodity_liability2,"
				+ "order_commodity_liability3,order_commodity_liability4,order_commodity_liability5,"
				+ "order_commodity_liability6,order_commodity_liability7,order_beneficiary_name,"
				+ "order_beneficiary_city,order_beneficiary_phone,order_commodity_time_start,"
				+ "order_commodity_time_end,order_commodity_time_length,order_sum_price) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			return JDBCUTILE.upDataBaseTable(sql,
					info.getOrder_id(),
					info.getOrder_commodity_liability1(),
					info.getOrder_commodity_liability2(),
					info.getOrder_commodity_liability3(),
					info.getOrder_commodity_liability4(),
					info.getOrder_commodity_liability5(),
					info.getOrder_commodity_liability6(),
					info.getOrder_commodity_liability7(),
					info.getOrder_beneficiary_name(),
					info.getOrder_beneficiary_city(),
					info.getOrder_beneficiary_phone(),
					info.getOrder_commodity_time_start(),
					info.getOrder_commodity_time_end(),
					info.getOrder_commodity_time_length(),
					info.getOrder_sum_price());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { JDBCUTILE.dbClose(); } catch (Exception e) { e.printStackTrace(); }
		}
		return false;
	}
	
	/**
	 * 4、更新用户余额
	 */
	public boolean updateUserMoney(Integer user_id, String money) {
		String sql = "UPDATE my_user_info SET user_money = ? WHERE user_id = ?";
		try {
			return JDBCUTILE.upDataBaseTable(sql, money, user_id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { JDBCUTILE.dbClose(); 
			} catch (Exception e) { 
				e.printStackTrace(); }
		}
		return false;
	}
	
	/**
	 * 5、按用户ID查订单列表（三表联查，显示订单信息）
	 */
	public List<My_order_info> selectOrderByUserId(Integer user_id) {
		String sql = "SELECT o.order_id, o.user_id, o.commodity_id, oi.order_info_id, oi.order_sum_price, "
				+ "oi.order_beneficiary_name, oi.order_beneficiary_city, oi.order_beneficiary_phone, "
				+ "oi.order_commodity_time_start, oi.order_commodity_time_end, oi.order_commodity_time_length, "
				+ "c.commodity_name, u.user_name "
				+ "FROM my_order o "
				+ "JOIN my_order_info oi ON o.order_id = oi.order_id "
				+ "JOIN commodity c ON o.commodity_id = c.commodity_id "
				+ "JOIN my_user_info u ON o.user_id = u.user_id "
				+ "WHERE o.user_id = ?";
		ResultSet rest = null;
		List<My_order_info> list = new ArrayList<My_order_info>();
		try {
			rest = JDBCUTILE.selectDataBaseTable(sql, user_id);
			while(rest.next()) {
				My_order_info o = new My_order_info(
						rest.getString("order_id"),
						rest.getInt("user_id"),
						rest.getInt("commodity_id"));
				o.setOrder_info_id(rest.getInt("order_info_id"));
				o.setOrder_sum_price(rest.getString("order_sum_price"));
				o.setOrder_beneficiary_name(rest.getString("order_beneficiary_name"));
				o.setOrder_beneficiary_city(rest.getString("order_beneficiary_city"));
				o.setOrder_beneficiary_phone(rest.getString("order_beneficiary_phone"));
				o.setOrder_commodity_time_start(rest.getString("order_commodity_time_start"));
				o.setOrder_commodity_time_end(rest.getString("order_commodity_time_end"));
				o.setOrder_commodity_time_length(rest.getString("order_commodity_time_length"));
				o.setCommodity_name(rest.getString("commodity_name"));
				o.setUser_name(rest.getString("user_name"));
				list.add(o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { JDBCUTILE.dbClose(); 
			} catch (Exception e) {
				e.printStackTrace(); }
		}
		return list;
	}
}


