package com.seiryo.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.seiryo.entity.AdminCommodity;
import com.seiryo.entity.AdminUser;
import com.seiryo.service.AdminCommodityService;
import com.seiryo.service.impl.AdminCommodityServiceImpl;

/**
 * 商品管理 Servlet（后台）
 * @ClassName: AdminCommodityServlet  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 李 
 * @date 2026年10月22日  
 *
 */
@WebServlet("/AdminCommodityServlet")
public class AdminCommodityServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private AdminCommodityService adminCommodityService = new AdminCommodityServiceImpl();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// 编码格式（也可以通过过滤器设置）
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");

		// 通过判断进行设置调用方法
		String opt = req.getParameter("opt");
		if (opt == null || opt.equals("")) {
			opt = "admin_commodity_list";
		}

		if ("admin_commodity_list".equals(opt)) {
			commodityList(req, resp);
		} else if ("admin_commodity_add".equals(opt)) {
			commodityAdd(req, resp);
		} else if ("admin_commodity_update".equals(opt)) {
			commodityUpdate(req, resp);
		} else if ("admin_commodity_status".equals(opt)) {
			commodityStatus(req, resp);
		} else if ("admin_commodity_delete".equals(opt)) {
			commodityDelete(req, resp);
		}
	}

	/**
	 * 商品一览表
	 * @Title: commodityList
	 * @Description: 按商品名、类型筛选，结果放进 request 后转发到列表页
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void commodityList(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//判断是否登录，没登录就返回登录页面
		if (!isLogin(req)) {
			resp.sendRedirect(req.getContextPath() + "/admin_login.jsp");
			return;
		}

		// keyword 输数字就是按 ID 查，输文字就是按商品名模糊查（判断在 Dao 里做）
		String keyword = req.getParameter("keyword");
		String type    = req.getParameter("type");

		List<AdminCommodity> commodityList = adminCommodityService.getCommodityList(keyword, type);

		req.setAttribute("commodityList", commodityList);
		// 把查询条件回传，页面上下拉框和输入框才能保持用户选的值
		req.setAttribute("keyword", keyword == null ? "" : keyword);
		req.setAttribute("type",    type    == null ? "" : type);
		req.getRequestDispatcher("/admin_commodity_list.jsp").forward(req, resp);
	}

	/**
	 * 新增商品
	 * @Title: commodityAdd
	 * @Description: 取表单里的 8 个字段，交给 Service 校验并入库
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void commodityAdd(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//判断是否登录，没登录就返回登录页面
		if (!isLogin(req)) {
			resp.getWriter().print(0);
			return;
		}

		String code = adminCommodityService.addCommodity(
				req.getParameter("name"),
				req.getParameter("price"),
				req.getParameter("type"),
				req.getParameter("img"),
				req.getParameter("introduce"),
				req.getParameter("timeLength"),
				req.getParameter("ageStart"),
				req.getParameter("ageEnd"));
		//打印
		resp.getWriter().print(code);
	}

	/**
	 * 修改商品
	 * @Title: commodityUpdate
	 * @Description: 和新增用同一套字段，多一个 commodityId
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void commodityUpdate(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (!isLogin(req)) {
			resp.getWriter().print(0);
			return;
		}

		String code = adminCommodityService.updateCommodity(
				req.getParameter("commodityId"),
				req.getParameter("name"),
				req.getParameter("price"),
				req.getParameter("type"),
				req.getParameter("img"),
				req.getParameter("introduce"),
				req.getParameter("timeLength"),
				req.getParameter("ageStart"),
				req.getParameter("ageEnd"));

		resp.getWriter().print(code);
	}

	/**
	 * 上架 / 下架
	 * @Title: commodityStatus
	 * @Description: status=1 上架，status=0 下架
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void commodityStatus(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (!isLogin(req)) {
			resp.getWriter().print(0);
			return;
		}
		//根据商品id修改商品状态
		String code = adminCommodityService.changeStatus(
				req.getParameter("commodityId"), req.getParameter("status"));

		resp.getWriter().print(code);
	}

	/**
	 * 删除商品
	 * @Title: commodityDelete
	 * @Description: 已经卖过的商品不让删（Service 返回 3），只能下架
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void commodityDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (!isLogin(req)) {
			resp.getWriter().print(0);
			return;
		}

		String code = adminCommodityService.deleteCommodity(req.getParameter("commodityId"));
		resp.getWriter().print(code);
	}

	/**
	 * 判断是否已登录
	 * 因为可以通过直接opt地址来管理商品页面，所以要限制一下，只有登录了才能操作
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

