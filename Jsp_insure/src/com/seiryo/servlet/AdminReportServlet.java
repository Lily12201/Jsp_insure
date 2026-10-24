package com.seiryo.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.seiryo.dao.AdminReportDao;
import com.seiryo.dao.AdminUserDao;
import com.seiryo.dao.impl.AdminReportDaoImpl;
import com.seiryo.dao.impl.AdminUserDaoImpl;
import com.seiryo.entity.AdminUser;
import com.seiryo.entity.MyUserInfo;
import com.seiryo.entity.My_order_info;
import com.seiryo.entity.Statltem;
import com.seiryo.service.AdminOrderService;
import com.seiryo.service.impl.AdminOrderServiceImpl;
import com.seiryo.utile.CsvExportUtil;
/**
 * Servlet类
 * @ClassName: AdminReportServlet  
 * @Description: 看报表/导出报表
 * @author 李 
 * @date 2026年10月23日  
 * 导出类型：sex/age/city/type/month/user/order
 * 注意：这里的导出报表只能用：resp.getOutputStream()写文件，不能用getWriter()或转发页面
 * 不用service层校验和拦截，直接调用dao层数据
 */
@WebServlet("/AdminReportServlet")
public class AdminReportServlet extends HttpServlet {
	private AdminReportDao    reportDao    = new AdminReportDaoImpl();
	private AdminUserDao      userDao      = new AdminUserDaoImpl();
	private AdminOrderService orderService = new AdminOrderServiceImpl();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	//1、设置编码格式
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;chartset=UTF-8");
		
	//2、通过判断进行调用方法设置
		String opt=req.getParameter("opt");
		if("export".equals(opt)) {
			//导出
			export(req,resp);
		}else {
			view(req,resp);
		}
	
	}
/**
 * 页面报表显示：查出报表，塞进request，转发给jsp	
 * @Title: view  
 * @Description: 对应jsp页面的sexList/ageList/cityList/typeList/monthList
 * @param @param req
 * @param @param resp
 * @param @throws ServletException
 * @param @throws IOException    参数  
 * @return void    返回类型  
 * @throws
 */
	private void view(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1、先判断是否登录，不登录不能操作
		if(!isLogin(req)) {
			resp.sendRedirect(req.getContextPath()+"/admin_login.jsp");
			return;
		}
		//2、要去dao层调用查找的数据了，调用按照各个条件查询的报表
		req.setAttribute("sexList",   reportDao.selectSexStat());
		req.setAttribute("ageList",   reportDao.selectAgeStat());
		req.setAttribute("cityList",  reportDao.selectCityStat());
		req.setAttribute("typeList",  reportDao.selectTypeStat());
		req.setAttribute("monthList", reportDao.selectMonthStat());
		//请求转发到报表界面
		req.getRequestDispatcher("/admin_report.jsp").forward(req, resp);

		
	}
	
	/**
	 * 导出 CSV（Excel 能直接打开）
	 * @Title: export
	 * @Description: 按 type 决定导出哪张表，
	 *               每种报表准备「标题 + 表头 + 数据行」，最后统一交给 CsvExportUtil
	 * @param @param req
	 * @param @param resp    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void export(HttpServletRequest req, HttpServletResponse resp) {

		// 没登录：下载类的请求不能跳转页面，直接什么都不给
		if (!isLogin(req)) {
			return;
		}

		String type = req.getParameter("type");
		if (type == null) {
			type = "sex";
		}
		// 文件里第一行的大标题
		String          title   = "";  
		 // 表头
		String[]        headers = null;
		// 数据（一行一个 Object[]）
		List<Object[]>  rows    = new ArrayList<Object[]>();   

		// ---------- 按 type 决定导出哪张表 ----------
		if ("sex".equals(type)) {
			title   = "ユーザー性別分布統計";
			headers = new String[] { "性別", "人数", "割合(%)" };
			for (Statltem it : reportDao.selectSexStat()) {
				rows.add(new Object[] { it.getLabel(), it.getValue(), it.getPercent() });
			}

		} else if ("age".equals(type)) {
			title   = "ユーザー年齢分布統計";
			headers = new String[] { "年齢層", "人数", "割合(%)" };
			for (Statltem it : reportDao.selectAgeStat()) {
				rows.add(new Object[] { it.getLabel(), it.getValue(), it.getPercent() });
			}

		} else if ("city".equals(type)) {
			title   = "被保険者地域分布統計";
			headers = new String[] { "地域", "件数", "割合(%)" };
			for (Statltem it : reportDao.selectCityStat()) {
				rows.add(new Object[] { it.getLabel(), it.getValue(), it.getPercent() });
			}

		} else if ("type".equals(type)) {
			title   = "商品タイプ別売上統計";
			headers = new String[] { "商品タイプ", "注文数", "売上金額(円)" };
			for (Statltem it : reportDao.selectTypeStat()) {
				rows.add(new Object[] { it.getLabel(), it.getValue(), it.getAmount() });
			}

		} else if ("month".equals(type)) {
			title   = "月次売上推移";
			headers = new String[] { "年月", "注文数", "売上金額(円)" };
			for (Statltem it : reportDao.selectMonthStat()) {
				rows.add(new Object[] { it.getLabel(), it.getValue(), it.getAmount() });
			}

		} else if ("user".equals(type)) {
			title   = "会員一覧";
			headers = new String[] { "ID", "氏名", "メール", "電話番号",
			                         "性別", "生年月日", "残高(円)", "状態" };
			
			// 传 null = 不加任何查询条件，查出全部会员
			List<MyUserInfo> users = userDao.selectUserList(null);
			for (MyUserInfo u : users) {
				int money = 0;
				try {
					// user_money 是 VARCHAR，要转成数字（空值或乱值时兜底为 0）
					money = (int) Double.parseDouble(u.getUser_money());
				} catch (Exception e) {
					money = 0;
				}
				rows.add(new Object[] {
						u.getUser_id(),
						u.getUser_name(),
						u.getUser_email(),
						u.getUser_phone(),
						u.getUser_sex(),
						u.getUser_birthday(),
						money,
						u.getUser_status() != null && u.getUser_status().intValue() == 1 ? "有効" : "無効"
				});
			}

		} else if ("order".equals(type)) {
			title   = "注文一覧";
			headers = new String[] { "注文番号", "契約者", "商品名", "被保険者",
			                         "投保地", "電話番号", "保険期間", "年数", "金額(円)" };
			
			// 三个条件都传 null = 查全部订单
			List<My_order_info> orders = orderService.getOrderList(null, null, null);
			for (My_order_info o : orders) {
				rows.add(new Object[] {
						o.getOrder_id(),
						o.getUser_name(),
						o.getCommodity_name(),
						o.getOrder_beneficiary_name(),
						o.getOrder_beneficiary_city(),
						o.getOrder_beneficiary_phone(),
						o.getOrder_commodity_time_start() + " 〜 " + o.getOrder_commodity_time_end(),
						o.getOrder_commodity_time_length(),
						o.getOrder_sum_price()
				});
			}

		} else {
			title   = "統計レポート";
			headers = new String[] { "項目" };
			rows.add(new Object[] { "該当するレポートがありません" });
		}

		// CVS交给工具类写文件
		try {
			CsvExportUtil.export(req, resp, title, headers, rows, title);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


/**
 * isLogin判断你是否登录的小方法
 * @Title: isLogin  
 * @Description: TODO(这里用一句话描述这个方法的作用)  
 * @param @param req
 * @param @return    参数  
 * @return boolean    返回类型  
 * @throws
 */
	private boolean isLogin(HttpServletRequest req) {
		HttpSession session=req.getSession();
		AdminUser admin=(AdminUser)session.getAttribute(AdminLoginServlet.SESSION_ADMIN);
		return admin!=null;
	}
	
}
