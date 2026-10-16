package com.seiryo.service;

import java.util.List;

import com.seiryo.entity.MyUserInfo;

/**
 * 
 * @ClassName: UserService  
 * @Description: TODO(用户服务层接口)  
 * @author 10748  
 * @date 2025年10月14日
 */
public interface UserService {
	
	//插入用户
	boolean insertUser(MyUserInfo myUserInfo);
	
	//查询所有用户信息
	List<MyUserInfo> selectAllUserInfo();
	
	//判断用户邮箱是否重复
	boolean pdUserEmail(String email);
	
	//用户登录
	MyUserInfo pdUserLogin(String email,String pass);
	
	//修改用户信息
	boolean updateUserInfo(Integer user_id, Integer user_info_id,String userPass, String userPhone, String userMoney);

}
