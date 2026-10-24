package com.seiryo.servlet;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;   


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.seiryo.entity.AdminUser;
import com.seiryo.service.AdminLoginService;
import com.seiryo.service.impl.AdminLoginServiceImpl;
/**
 * 后台登录、注册、退出
 * @ClassName: AdminUserServlet  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 李 
 * @date 2026年10月21日  
 *
 */
@WebServlet("/AdminLoginServlet")  
public class AdminLoginServlet extends HttpServlet {
	
	private AdminLoginService adminLoginService=new AdminLoginServiceImpl();
	
	//session里存管理员的key，全项目可以统一调用；因为Session的机制是依靠字符串（String）作为key来存取数据的
	public static final String SESSION_ADMIN="adminUser";
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//编码格式（也可以通过过滤器设置）
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");
		//通过判断进行设置调用方法
		String opt=req.getParameter("opt");
		if("admin_login".equals(opt)) {
			adminLogin(req,resp);			
		}else if("admin_register".equals(opt)) {
			adminRegister(req,resp);
		}else if("admin_logout".equals(opt)) {
			adminLogout(req,resp);
		}
		
		
	}
	/**
	 * 登录
	 * @Title: Login  
	 * @Description: TODO(这里用一句话描述这个方法的作用)  
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数  
	 * @return void    返回类型  
	 * @throws
	 */
	private void adminLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		//1、获取前端输入的账号和密码
		String account=req.getParameter("account");
		String password=req.getParameter("password");
		
		//2、非空判断（前端已经判断过一次，这是二次防线）
		if(account==null||account.equals("")||password==null||password.equals("")) {
			resp.getWriter().print(0);
			return;			
		}
		//3、调用Service校验，通过与数据库的账号密码对比，返回为null表示账号或密码错误
		AdminUser admin=adminLoginService.adminLogin(account, password);
		if(admin==null) {
			resp.getWriter().print(0);
			return;
		}
		//4、账号存在，密码对了，但是被禁用了
		if(admin.getAdmin_status()!=null&&admin.getAdmin_status().intValue()==0) {
			resp.getWriter().print(2);
			return;
		}
		//5、登录成功：存进session
		HttpSession session=req.getSession();
		session.setAttribute(SESSION_ADMIN, admin);
		//6、30分支不操作就得重新登录
		session.setMaxInactiveInterval(30*60);
		resp.getWriter().print(1);		
	}
/**
 * 后台注册（新增管理员账号）	
 * @Title: adminRegister  
 * @Description: TODO(这里用一句话描述这个方法的作用)  
 * @param @param req
 * @param @param resp
 * @param @throws ServletException
 * @param @throws IOException    参数  
 * @return void    返回类型  
 * @throws
 */
	private void adminRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		
	//1、获取前端数据
	String account=req.getParameter("account");
	String password=req.getParameter("password");
	String name=req.getParameter("name");
	String regCode=req.getParameter("regCode");
	
	//2、调用Service（非空校验、查重、注册码校验都在里面）
	int result=adminLoginService.adminRegister(account, password, name, regCode);
	
	//3、把结果返回给前端
	//1成功；0账号已经存在；2输入不合法；3注册码不对；-1数据库出错
	resp.getWriter().print(result);		
		
	}

/**
 * 后台退出	
 * @Title: adminLogout  
 * @Description: 整个 session 作废，回到登录页 
 * @param @param req
 * @param @param resp
 * @param @throws ServletException
 * @param @throws IOException    参数  
 * @return void    返回类型  
 * @throws
 */
	private void adminLogout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		
		//为了管理员账号的安全性，每次退出登录之后，把session里的数据都销毁，这样避免账号信息泄露
		HttpSession session=req.getSession();
		if(session!=null) {
			session.invalidate();
		}
		resp.sendRedirect(req.getContextPath()+"/admin_login.jsp");
	}
		
	
}
