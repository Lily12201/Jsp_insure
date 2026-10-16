package com.seiryo.servlet;

import java.io.IOException;

import com.seiryo.entity.MyUserInfo;
import com.seiryo.service.UserService;
import com.seiryo.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private UserService userService = new UserServiceImpl();
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//a获取判断
		String opt = req.getParameter("opt");
		//b通过判断进行设置调用方法
		if("user_regist".equals(opt)) {
			userRegister(req,resp);
		}else if("user_login".equals(opt)) {
			userLogin(req,resp);
		}else if("user_update".equals(opt)) {
		    userUpdate(req,resp);
		}

		
	}
	
	/**
	 * user_login
	 * @param req 请求
	 * @param resp 响应
	 * @throws ServletException 异常
	 * @throws IOException 异常
	 */
	private void userLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		
		//获取前台传递的内容
		String user_email = req.getParameter("userEmail");
		String user_pass = req.getParameter("userPass");
		//通过账号密码判断登录
		MyUserInfo user = userService.pdUserLogin(user_email, user_pass);
		if(user != null) {
			HttpSession session = req.getSession();
			session.setAttribute("user", user);
			resp.getWriter().println(1);
		}else {
			resp.getWriter().println(0);
		}
	}
	
	/**
	 * user_regist
	 * @param req 请求
	 * @param resp 响应
	 * @throws ServletException 异常
	 * @throws IOException 异常
	 */
	private void userRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		
		//获取传递的数据
		String userName = req.getParameter("userName");
		String userBirthday = req.getParameter("userBirthday");
		String userSex = req.getParameter("userSex");
		String userPhone = req.getParameter("userPhone");
		String userEmail = req.getParameter("userEmail");
		String userPass = req.getParameter("userPass");
		if("1".equals(userSex)) {
			userSex = "男";
		}else {
			userSex = "女";
		}
		//设置初始化参数
		int a = 0;
		//调用判断账号是否重复的方法
		if(!userService.pdUserEmail(userEmail)) {
			//创建新的对象进行保存用户信息（保存前端注册的账号数据）
			MyUserInfo myUserInfo = new MyUserInfo(null,userEmail,userPass, null, userPhone, "0", userName, userBirthday,userSex, 1);
			//插入用户信息
			if(userService.insertUser(myUserInfo)) {
				a = 1;
			}
		}
		resp.getWriter().println(a);
	}
	
	/**
	 * 修改用户信息（密码+手机号+充值）
	 * @Title: userUpdate  
	 * @Description: TODO(这里用一句话描述这个方法的作用)  
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数  
	 * @return void    返回类型  
	 * @throws
	 */
	private void userUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
	    //1、取前端数据
	    String userPass = req.getParameter("userPass");
	    String userPhone = req.getParameter("userPhone");
	    String chargeMoney = req.getParameter("chargeMoney");
	    //2、取当前登录用户
	    HttpSession session = req.getSession();
	    MyUserInfo user = (MyUserInfo) session.getAttribute("user");
	    //3、没登录不允许修改
	    if(user == null) {
	        resp.getWriter().println(0);
	        return;
	    }
	    //4、调service更新（余额=原余额+充值）
	    int money = 0;
	    try {
	        money = Integer.parseInt(user.getUser_money() == null ? "0" : user.getUser_money())
	                + Integer.parseInt(chargeMoney);
	    } catch (NumberFormatException e) {
	        money = Integer.parseInt(chargeMoney);
	    }
	    //因为user_money为字符串类型，所以需要在更新数据库时，把参数变成字符串类型
	    boolean result = userService.updateUserInfo(user.getUser_id(), user.getUser_info_id(), userPass, userPhone, String.valueOf(money));
	    //5、成功后同步更新session里的用户数据（否则页面回显还是旧值）
	    if(result) {
	        user.setUser_password(userPass);
	        user.setUser_phone(userPhone);
	        user.setUser_money(String.valueOf(money));
	        session.setAttribute("user", user);
	        //注意：这里如果输出有换行符的话，返回前端是"1\n"，在JS中1\n=="1"是false
	        resp.getWriter().print(1);
	    }else {
	        resp.getWriter().println(0);
	    }
	}

	
}
