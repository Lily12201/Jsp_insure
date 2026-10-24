package com.seiryo.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.seiryo.entity.AdminUser;
import com.seiryo.service.AdminUserService;
import com.seiryo.service.impl.AdminUserServiceImpl;
import com.seiryo.entity.MyUserInfo;

/**
 * 用户管理Servlet控制台
 * @ClassName: AdminUserServlet  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 李 
 * @date 2026年10月22日  
 *
 */
@WebServlet("/AdminUserServlet")
public class AdminUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private AdminUserService adminUserService = new AdminUserServiceImpl();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// 编码格式（也可以通过过滤器设置）
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");

		// 通过判断进行设置调用方法
		String opt = req.getParameter("opt");
		if (opt == null || opt.equals("")) {
			opt = "admin_user_list";
		}

		if ("admin_user_list".equals(opt)) {
			userList(req, resp);
		} else if ("admin_user_status".equals(opt)) {
			changeStatus(req, resp);
		} else if ("admin_user_charge".equals(opt)) {
			recharge(req, resp);
		} else if ("admin_user_delete".equals(opt)) {
			deleteUser(req, resp);
		}
	}

	/**
	 * 用户一览表
	 * @Title: userList
	 * @Description: 按姓名模糊查询会员，结果放进 request 后转发到列表页（这一步是页面跳转，不是 AJAX）
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void userList(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// 没登录：页面类的请求 → 直接跳登录页
		if (!isLogin(req)) {
			resp.sendRedirect(req.getContextPath() + "/admin_login.jsp");
			return;
		}

		// keyword 输数字就是按 ID 查，输文字就是按姓名模糊查（判断在 Dao 里做）
		String keyword = req.getParameter("keyword");
		List<MyUserInfo> userList = adminUserService.getUserList(keyword);

		req.setAttribute("userList", userList);
		req.setAttribute("keyword", keyword == null ? "" : keyword);
		req.getRequestDispatcher("/admin_user_list.jsp").forward(req, resp);
	}

	/**
	 * 禁用 / 启用会员
	 * @Title: changeStatus
	 * @Description: 改 my_user_info.user_status（1 有効 / 0 無効）
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void changeStatus(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// 没登录：AJAX 类的请求返回 0，前端弹「失败」
		if (!isLogin(req)) {
			resp.getWriter().print(0);
			return;
		}

		String code = adminUserService.changeStatus(
				req.getParameter("userInfoId"), req.getParameter("status"));

		// 注意：必须用 print 不能用 println（println 会多一个换行符，JS 判断会失败）
		resp.getWriter().print(code);
	}

	/**
	 * 会员充值
	 * @Title: recharge
	 * @Description: 在原余额基础上加上充值金额（加法在 Service 里算）
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void recharge(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (!isLogin(req)) {
			resp.getWriter().print(0);
			return;
		}

		String code = adminUserService.recharge(
				req.getParameter("userInfoId"), req.getParameter("addMoney"));
		resp.getWriter().print(code);
	}

	/**
	 * 删除会员
	 * @Title: deleteUser
	 * @Description: 先删 my_user_info 再删 my_user（顺序反了会留下垃圾数据）
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void deleteUser(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (!isLogin(req)) {
			resp.getWriter().print(0);
			return;
		}

		String code = adminUserService.deleteUser(
				req.getParameter("userId"), req.getParameter("userInfoId"));
		resp.getWriter().print(code);
	}

	/**
	 * 判断是否已登录
	 * @Title: isLogin
	 * @Description: 从 session 里取管理员，取不到就是没登录
	 * @param @param req 参数
	 * @return boolean    返回类型
	 * @throws
	 */
	private boolean isLogin(HttpServletRequest req) {
		HttpSession session = req.getSession();
		AdminUser admin = (AdminUser) session.getAttribute(AdminLoginServlet.SESSION_ADMIN);
		return admin != null;
	}
}
