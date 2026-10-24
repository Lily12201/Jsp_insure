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


	    //1、取当前登录用户
	    HttpSession session = req.getSession();
	    MyUserInfo user = (MyUserInfo) session.getAttribute("user");
	    if (user == null) {
	        resp.getWriter().print("0");
	        return;
	    }

	    //2、取前端数据
	    String userPass    = req.getParameter("userPass");
	    String userPhone   = req.getParameter("userPhone");
	    String chargeMoney = req.getParameter("chargeMoney");

	    //3、服务端二次校验
	    if (userPass == null || !userPass.matches("\\w{6,16}")) {
	        resp.getWriter().print("0");
	        return;
	    }
	    if (userPhone == null || !userPhone.matches("1\\d{10}")) {
	        resp.getWriter().print("0");
	        return;
	    }

	    //4、解析原余额（空/非法一律当 0）
	    //余额是 String 要转数值、long 范围大防溢出、和前端 Number 精度匹配
	    //0L 明确告诉读代码的人"这是 long"，不用去猜。
	    long oldMoney = 0L;
	    String oldMoneyStr = user.getUser_money();
	    if (oldMoneyStr != null && !oldMoneyStr.trim().isEmpty()) {
	        try { oldMoney = Long.parseLong(oldMoneyStr.trim()); }
	        catch (NumberFormatException e) { oldMoney = 0L; }
	    }

	    //5、解析充值金额：null / 空 / 非法 / <=0 都视为"不充值"
	    long charge = 0L;
	    if (chargeMoney != null && !chargeMoney.trim().isEmpty()) {
	        try { charge = Long.parseLong(chargeMoney.trim()); }
	        catch (NumberFormatException e) { charge = 0L; }
	    }
	    if (charge < 0) charge = 0L;

	    //6、新余额：只有 charge > 0 时才真正增加
	    long newMoney = oldMoney + charge;

	    //7、调 service 更新
	    boolean result = userService.updateUserInfo(
	            user.getUser_id(),
	            user.getUser_info_id(),
	            userPass,
	            userPhone,
	            String.valueOf(newMoney));

	    //8、成功后同步 session（否则页面回显旧值）
	    if (result) {
	        user.setUser_password(userPass);
	        user.setUser_phone(userPhone);
	        user.setUser_money(String.valueOf(newMoney));
	        session.setAttribute("user", user);
	       // 必须用 print，不能用 println
	        resp.getWriter().print("1");      
	    } else {
	        resp.getWriter().print("0");
	    }
	}
	
}
