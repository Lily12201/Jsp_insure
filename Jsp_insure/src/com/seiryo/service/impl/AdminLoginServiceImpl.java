package com.seiryo.service.impl;

import com.seiryo.dao.AdminLoginDao;
import com.seiryo.dao.impl.AdminLoginDaoImpl;
import com.seiryo.entity.AdminUser;
import com.seiryo.service.AdminLoginService;
/**
 * 校验输入、查重、调Dao层、把结果翻译成数字
 * @ClassName: AdminLoginServiceImpl  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 李 
 * @date 2026年10月21日  
 *
 */
public class AdminLoginServiceImpl implements AdminLoginService{
	
	//调用Dao层数据
	private AdminLoginDao adminLoginDao=new AdminLoginDaoImpl();
	
	//注册码：只有知道这串文字的人才能注册管理员信息
	//因为注册页面任何人都能注册进入的话，外人就可以随便注册，并看订单数据
	private static final String REG_CODE="admin2026";
/**
 * 登录业务逻辑	
 * <p>Title: adminLogin</p>  
 * <p>Description: </p>  
 * @param account
 * @param password
 * @return  
 * @see com.seiryo.service.AdminLoginService#adminLogin(java.lang.String, java.lang.String)
 */
	public AdminUser adminLogin(String account,String password) {
		//1、非空判断（前端判断一遍）
		if(account==null||account.equals("")||password==null||password.equals("")) {
			return null;			
		}
		//2、按照账号查询
		AdminUser admin=adminLoginDao.selectByAccount(account.trim());
		if(admin==null) {
			return null;
		}
		//3、对比密码
		if(!password.equals(admin.getAdmin_password())) {
			return null;
		}
		//4、账号和密码都对时返回对象，是否被停用交给Servlet判断
		return admin;
	}
/**
 * 管理员账号注册页面	
 */
public int adminRegister(String account,String password,String name,String regCode) {
	//1、上面设置的登录页面注册码必须填对，不然不可以注册
	if(regCode==null||!REG_CODE.equals(regCode.trim())) {
		return 3;
	}
	//2、账号3~20位
	if(account==null||account.trim().length()<3||account.trim().length()>20) {
		return 2;
	}
	//3、密码6~20位
	if(password==null||password.trim().length()<6||password.trim().length()>20) {
		return 2;
	}
	//4、姓名可以为空，空的话就填「（未設定）」
	if(name==null||name.trim().equals("")) {
		name = "（未設定）";
	}
	//5、查重（因为我在数据库中就是查的是输入参数的相同账号数量，返回的是数字，所以可以直接对比）
	if(adminLoginDao.countByAccount(account.trim())>0){
		return 0;
	}
	//6、新增。状态默认为1（启用），注册完就可以直接登录
	// 自己注册的，权限一律是「一般管理者」(2)。
	//   不给他选总管理员的机会——不然谁都能注册成总管理员，权限就形同虚设了。
    //   想当总管理员，必须让现有的总管理员在后台把你「総管理に上げる」。
	AdminUser admin=new AdminUser();
	admin.setAdmin_account(account.trim());
	admin.setAdmin_password(password.trim());
	admin.setAdmin_name(name.trim());
	admin.setAdmin_status(1);
	admin.setAdmin_role(2);
	
	boolean ok=adminLoginDao.insertAccount(admin);
	return ok ? 1:-1;
	
	
}
}
