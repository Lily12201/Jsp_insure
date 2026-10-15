package com.seiryo.servlet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import com.seiryo.entity.Commodity;
import com.seiryo.entity.Commodity_liability_info;
import com.seiryo.entity.MyUserInfo;
import com.seiryo.service.CommodityService;
import com.seiryo.service.impl.CommodityServiceImpl;
import com.seiryo.utile.JSONUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;

@WebServlet("/CommodityServlet")
public class CommodityServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	private CommodityService commodityService = new CommodityServiceImpl();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1、获取判断
		String opt=req.getParameter("opt");
		//2、通过判断进行设置调用方法
		if("selectAllCommodityList".equals(opt)) {
			selectAllCommodityList(req,resp);
			
		}else if("selectCommodityListByType".equals(opt)) {
			selectCommodityListByType(req,resp);
		}else if("commodityQueryById".equals(opt)) {
			CommodityQuertById(req,resp);
		}
				
	}
	/**
	 * 1、
	 * @Title: selectAllCommodityList  
	 * @Description: 触发index.jsp中的点击事件，请求调用opt，走这个selectAllCommodityList 方法；
	 * 返回JSON数组，前端ajax succsee里拼HTML 
	 * @param @param req
	 * @param @param resp
	 * @param @throws ServletException
	 * @param @throws IOException    参数  
	 * @return void    返回类型  
	 * @throws
	 */
	private void selectAllCommodityList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1、获取session中的登录用户
		HttpSession session=req.getSession();
		MyUserInfo user=(MyUserInfo)session.getAttribute("user");
		//创建一个放获取商品的集合
		List<Commodity>list=null;
		//判断是否有登录用户
		if(user!=null) {
			//已有用户登录：按照年龄，性别推荐商品排序
			list=commodityService.selectCommodityListByUserInfo(user);
			
		}else {
			//没有登录：显示全部商品
			list=commodityService.selectAllCommodityList();
		}
		//输入JSON给前端Ajax
		writeJSON(resp,list);
		
	}
/**
 * 	2、
 * @Title: selectCommodityListByType  
 * @Description: 首页的分类展示：1健康2意外3企业4旅行5家计 
 * @param @param req
 * @param @param resp
 * @param @throws ServletException
 * @param @throws IOException    参数  
 * @return void    返回类型  
 * @throws
 */
	private void selectCommodityListByType(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		
		//1、获取前端传递的商品类型
		String commodity_type=req.getParameter("commodity_type");
		//2、创建保存商品数据的集合
		List<Commodity>list=new ArrayList<Commodity>();
		//3、判断为空不查询，直接返回空数组
		if(commodity_type!=null&&!"".equals(commodity_type)) {
			list=commodityService.selectCommodityListByType(Integer.parseInt(commodity_type));
			
		}
		//输入JSON给前端Ajax
		writeJSON(resp,list);
	}
/**
 * 3、	
 * @Title: CommodityQuertById  
 * @Description: 商品详情：查询后存入session，再转发到目标页面
 * pd=page_info,jsp只查商品；pd=shop.jsp连带保险责任一起查
 * dao层商品表和商品详情表联查
 * @param @param req
 * @param @param resp
 * @param @throws ServletException
 * @param @throws IOException    参数  
 * @return void    返回类型  
 * @throws
 */
private void CommodityQuertById(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
	
	//1、获取前端传来的数据
	String commodity_id =req.getParameter("commodity_id");
	String pd=req.getParameter("pd");
	//2、没传目标页面时，默认去商品详情页
	if(pd==null||"".equals(pd)) {
		pd="page_info.jsp";
	}
	
	//3、根据商品id查询商品
	Commodity commodity=null;
	if("shop.jsp".equals(pd)) {
		//查询1：购买页面要展示7条保险责任，所以需要调取dao层商品表和商品详情表联查(查的字符串转换成数字形式）
		commodity=commodityService.selectCommodityLiabilityInfoById(Integer.parseInt(commodity_id));
	}else {
		//查询2：详情页只需要展示商品本身
		commodity=commodityService.selectCommodityById(Integer.parseInt(commodity_id));
	}
	
	//4、将查询到的数据存到session，供页面EL表达式取值
	HttpSession session=req.getSession();
	session.setAttribute("commodityInfo", commodity);
	//5、转发到目标页面
	req.getRequestDispatcher(pd).forward(req, resp);
	
	
	
}
	
/**
 * 把商品集合转成JSON输出给前端Ajax	
 * @Title: writeJSON  
 * @Description: TODO(这里用一句话描述这个方法的作用)  
 * @param @param req
 * @param @param resp
 * @param @throws ServletException
 * @param @throws IOException    参数  
 * @return void    返回类型  
 * @throws
 */
	private void writeJSON(HttpServletResponse resp, List<Commodity>list) throws ServletException, IOException {
		
		//空集合兜底，避免出现null，导致前端data.length报错
		if(list==null) {
			list=new ArrayList<Commodity>();
		}
		try {
			resp.setContentType("application/json;charset=UTF-8");
			resp.getWriter().print(JSONUtil.toJSONString(list));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
