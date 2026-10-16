package com.seiryo.dao;

import java.util.List;

import com.seiryo.entity.MyUserInfo;
import com.seiryo.entity.My_order_info;

public interface OrderDao {
	
	//1、按姓名+手机号查用户（判断是否注册、手机号是否一致）
	MyUserInfo selectUserByNameAndPhone(String name,String phone);
	//再进步验证是用户手机号不符合，还是未登录
	MyUserInfo selectUserByName(String name);
	
	//2、插入订单主表 my_order
	boolean insertMyOrder(String order_id, Integer user_id, Integer commodity_id);
		
	//3、插入订单详情表 my_order_info
	boolean insertMyOrderInfo(My_order_info info);
	
	//4、更新用户余额
	boolean updateUserMoney(Integer user_id, String money);
	
	//5、按用户ID查订单列表（我的订单页面）
	List<My_order_info> selectOrderByUserId(Integer user_id);

}
