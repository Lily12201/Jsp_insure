package com.seiryo.dao;

import java.util.List;

import com.seiryo.entity.MyUserInfo;

/**
 * 
 * @ClassName: UserDao  
 * @Description: TODO(用户数据层接口)  
 * @author 10748  
 * @date 2025年10月14日
 */
public interface UserDao {
	
	//插入用户（注册使用）
	boolean insertUser(MyUserInfo myUserInfo);
	
	//查询所有用户信息（登录验证使用）
	List<MyUserInfo> selectAllUserInfo();
	
	//修改信息（my_info.jsp页面）
	boolean updateUserInfo(Integer user_id, Integer user_info_id, String userPass, String userPhone, String userMoney);

	

}
