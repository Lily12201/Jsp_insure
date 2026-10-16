package com.seiryo.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.seiryo.entity.MyUserInfo;
import com.seiryo.entity.My_order_info;
import com.seiryo.service.OrderService;
import com.seiryo.service.impl.OrderServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	private OrderService orderService = new OrderServiceImpl();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String opt=req.getParameter("opt");
		if("buyCommodity".equals(opt)) {
			buyCommodity(req,resp);
		}else if("select_order_by_user_id".equals(opt)) {
			selectOrderByUserId(req, resp);
		}
	}
/**
 * 购买保险	
 * @Title: buyCommodity  
 * @Description: TODO(这里用一句话描述这个方法的作用)  
 * @param @param req
 * @param @param resp
 * @param @throws ServletException
 * @param @throws IOException    参数  
 * @return void    返回类型  
 * @throws
 */
	private void buyCommodity(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		//1、必须登录才能购买
		HttpSession session=req.getSession();
		MyUserInfo user=(MyUserInfo)session.getAttribute("user");
		if(user==null) {
			resp.getWriter().print("0");
			return;
		}
		//2、获取前端数据
		My_order_info info = new My_order_info();
		Integer commodity_id=Integer.parseInt(req.getParameter("commodity_id"));
		String beneficiaryName=req.getParameter("beneficiaryName");
		String beneficiarylocation=req.getParameter("beneficiarylocation");
		String beneficiaryPhone=req.getParameter("beneficiaryPhone");
		String timeLength=req.getParameter("timeLength");
		String startDate=req.getParameter("startDate");
		String sumPrice=req.getParameter("sumPrice");
		System.out.println("收到参数：name=" + beneficiaryName + "，phone=" + beneficiaryPhone);

		//3、调service做全部业务校验
		String code = orderService.buyCommodity(user, info);
		
		//4、成功后同步session里的余额（页面回显要用）
		if("1".equals(code)) {
			session.setAttribute("user", user);
		}
		//5、输出返回码给前端
		resp.getWriter().println(code);
	}

		
	/**
	 * 2、我的订单列表
	 */
	private void selectOrderByUserId(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		
		// 1、获取当前会话，从 session 中取出登录用户信息
		HttpSession session = req.getSession();
		MyUserInfo user = (MyUserInfo) session.getAttribute("user");
		//2、没有订单就返回登录页面
		if(user == null) {
			resp.sendRedirect("login.jsp");
			return;
		}
		// 3、根据当前登录用户的 ID 查询订单列表
		List<My_order_info> list = orderService.selectOrderByUserId(user.getUser_id());
		
		// 4、排序：将“被保人是当前用户自己”的订单优先展示
	    // 创建两个集合：mine 存放自己的订单，others 存放其他人的订单
		List<My_order_info> mine = new ArrayList<My_order_info>();
		List<My_order_info> others = new ArrayList<My_order_info>();
		
		// 遍历查询到的订单，判断被保人姓名和手机号是否与当前登录用户一致
		for(My_order_info o : list) {
			if(user.getUser_name() != null && user.getUser_name().equals(o.getOrder_beneficiary_name())
					&& user.getUser_phone() != null && user.getUser_phone().equals(o.getOrder_beneficiary_phone())) {
				 
				// 被保人是当前用户自己，加入 mine
				mine.add(o);
			}else {
				// 被保人不是当前用户，加入 others
				others.add(o);
			}
		}
		
		 // 5、合并两个列表：自己的订单在前，其他人的订单在后
		List<My_order_info> result = new ArrayList<My_order_info>();
		result.addAll(mine);
		result.addAll(others);
		
		// 6、将排序后的订单列表存入 request 作用域，并转发到 my_order.jsp 页面展示
		req.setAttribute("orderList", result);
		req.getRequestDispatcher("my_order.jsp").forward(req, resp);
	}	
		
	
	
}
