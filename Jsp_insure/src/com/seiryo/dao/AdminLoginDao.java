package com.seiryo.dao;

import com.seiryo.entity.AdminUser;

public interface AdminLoginDao {
	
	//按照账号查管理员，，查不到返回null
	AdminUser selectByAccount(String account);
	
	//查询这个账号有没有被占用，返回0代表没有被占用，可以注册
	int countByAccount(String account);
	
	//注册：向admin_user插入一条管理员信息
	boolean insertAccount(AdminUser admin);

}
