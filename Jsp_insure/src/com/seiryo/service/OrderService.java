package com.seiryo.service;

import java.util.List;

import com.seiryo.entity.MyUserInfo;
import com.seiryo.entity.My_order_info;

public interface OrderService {
	
	//1、按姓名+手机号查用户（验证是否注册、手机号是否一致）
	MyUserInfo selectUserByNameAndPhone(String name, String phone);
		
	//2、购买保险：全部业务校验+下单+扣款，返回提示码
	String buyCommodity(MyUserInfo loginUser, My_order_info info);
	
	//3、按用户ID查订单列表
	List<My_order_info> selectOrderByUserId(Integer user_id);
	
	
	}


