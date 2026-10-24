package com.seiryo.service;

import com.seiryo.entity.AdminUser;

public interface AdminLoginService {
	
	//判断登录账号和密码是否正确
	//注意：账号被停用时也返回对象，由Servlet去判断status，这样页面才能提示：账号无效
	AdminUser adminLogin(String account,String password);
	
	//注册新管理员时
	//return 1 成功 / 0 账号已存在 / 2 输入不合法 / 3 登録コード不对 / -1 数据库出错
	int adminRegister(String account,String password,String name,String regCode);
}
