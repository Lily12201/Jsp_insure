package com.seiryo.service.impl;

import java.util.List;

import com.seiryo.dao.UserDao;
import com.seiryo.dao.impl.UserDaoImpl;
import com.seiryo.entity.MyUserInfo;
import com.seiryo.service.UserService;

/**
 * 
 * @ClassName: UserServiceImpl  
 * @Description: TODO(用户服务层实现类)  
 * @author 10748  
 * @date 2025年10月14日
 */
public class UserServiceImpl implements UserService{
	
	//接口指向实现类
	private UserDao userDao = new UserDaoImpl();

	/**
	 * 新增用户
	 */
	@Override
	public boolean insertUser(MyUserInfo myUserInfo) {
		return userDao.insertUser(myUserInfo);
	}

	/**
	 * 查询所有用户信息
	 */
	@Override
	public List<MyUserInfo> selectAllUserInfo() {
		return userDao.selectAllUserInfo();
	}

	/**
	 * 判断用户邮箱是否重复
	 */
	@Override
	public boolean pdUserEmail(String email) {
		//1、创建一个集合存储后台调取的查询所有用户的数据
		List<MyUserInfo> userList = userDao.selectAllUserInfo();
		//2、判断后台拿到了数据
		if(userList.size() > 0) {
			//3、循环遍历数据
			for(MyUserInfo user:userList) {
				//4、前端的输入数据和后端一样时
				if(user.getUser_email().equals(email)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 用户登录
	 */
	@Override
	public MyUserInfo pdUserLogin(String email, String pass) {
		List<MyUserInfo> userList = userDao.selectAllUserInfo();
		if(userList.size() > 0) {
			for(MyUserInfo user:userList) {
				//前端输入的账号和密码和后端拿到的数据一样，就成功登录
				if(user.getUser_email().equals(email)) {
					if(user.getUser_password().equals(pass)) {
						return user;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 修改用户信息
	 */
	@Override
	public boolean updateUserInfo(Integer user_id, Integer user_info_id,String userPass, String userPhone, String userMoney) {
	    return userDao.updateUserInfo(user_id, user_info_id, userPass, userPhone, userMoney);
	}

}
