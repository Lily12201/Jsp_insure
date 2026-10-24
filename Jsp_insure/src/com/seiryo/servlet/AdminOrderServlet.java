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
import com.seiryo.entity.My_order_info;
import com.seiryo.service.AdminOrderService;
import com.seiryo.service.impl.AdminOrderServiceImpl;
/**
 * Servlet控制台
 * @ClassName: AdminOrderServlet  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 李 
 * @date 2026年10月23日  
 *  opt=admin_order_list    订单一览表（页面跳转，不是 AJAX）
 *                           支持三个查询条件：订单号 / 被保人姓名 / 手机号
 *  opt=admin_order_delete   删除订单（AJAX，返回 1 / 2 / 0）
 */
@WebServlet("/AdminOrderServlet")
public class AdminOrderServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private AdminOrderService adminOrderService = new AdminOrderServiceImpl();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// 编码格式（也可以通过过滤器设置）
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");

		// 通过判断进行设置调用方法
		String opt = req.getParameter("opt");
		if (opt == null || opt.equals("")) {
			opt = "admin_order_list";
		}

		if ("admin_order_list".equals(opt)) {
			orderList(req, resp);
		} else if ("admin_order_delete".equals(opt)) {
			orderDelete(req, resp);
		}
	}

	/**
	 *1、 订单一览表
	 * @Title: orderList
	 * @Description: 按订单号 / 被保人姓名 / 手机号三个条件筛选，全部为空就查全部订单。
	 *               查完放进 request 后转发到列表页（这一步是页面跳转，不是 AJAX）
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void orderList(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// 1、没登录：页面类的请求 → 直接跳登录页
		if (!isLogin(req)) {
			resp.sendRedirect(req.getContextPath() + "/admin_login.jsp");
			return;
		}
		//2、获取前端输入框的内容：判断
		// 三个查询条件，没填就是 null，Dao 里会判断「为空就不按它筛」
		String orderId         = req.getParameter("orderId");
		String beneficiaryName = req.getParameter("beneficiaryName");
		String phone           = req.getParameter("phone");
		
		//3、调用dao层数据：直接去写dao层和service，然后再回来补Servlet
		List<My_order_info> orderList =
				adminOrderService.getOrderList(orderId, beneficiaryName, phone);

		// 列表数据
		req.setAttribute("orderList", orderList);

		// 把查询条件原样传回页面：不然点「検索」后输入框会清空，用户不知道自己搜了什么
		// （三元运算符：null 就传空字符串，避免页面上显示 "null"）
		req.setAttribute("orderId",         orderId         == null ? "" : orderId);
		req.setAttribute("beneficiaryName", beneficiaryName == null ? "" : beneficiaryName);
		req.setAttribute("phone",           phone           == null ? "" : phone);

		// 顶部两个汇总数字
		req.setAttribute("totalCount", adminOrderService.countOrder());
		req.setAttribute("totalPrice", adminOrderService.sumOrderPrice());

		// 转发到订单列表页
		req.getRequestDispatcher("/admin_order_list.jsp").forward(req, resp);
	}

	/**
	 *2、 删除订单
	 * @Title: orderDelete
	 * @Description: 按订单号删除，判空在 Service 里做
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void orderDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// 没登录：AJAX 类的请求不能跳转，只能回一个数字，让前端自己弹提示
		if (!isLogin(req)) {
			resp.getWriter().print(0);
			return;
		}

		String code = adminOrderService.deleteOrder(req.getParameter("orderId"));

		// 注意：必须用 print 不能用 println（println 会多输出一个换行符，JS 里 date == 1 会判断失败）
		resp.getWriter().print(code);
	}

	/**
	 * 3、判断是否已登录
	 * @Title: isLogin
	 * @Description: 从 session 里取管理员，取不到就是没登录。
	 *               "adminUser" 这个 key 是 AdminLoginServlet.SESSION_ADMIN 定义的常量
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
