package com.seiryo.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.seiryo.dao.CommodityDao;
import com.seiryo.dao.OrderDao;
import com.seiryo.dao.impl.CommodityDaoImpl;
import com.seiryo.dao.impl.OrderDaoImpl;
import com.seiryo.entity.Commodity;
import com.seiryo.entity.MyUserInfo;
import com.seiryo.entity.My_order_info;
import com.seiryo.service.OrderService;

public class OrderServiceImpl implements OrderService{
	
	private OrderDao orderDao=new OrderDaoImpl();
	private CommodityDao commodityDao = new CommodityDaoImpl();
	
	/**
	 * 1、按姓名+手机号查用户（判断是否登录）
	 */
	public MyUserInfo selectUserByNameAndPhone(String name,String phone) {
		return orderDao.selectUserByNameAndPhone(name, phone);
	}
	
	
	/**
	 * 2、购买保险：全部业务校验
	 * 返回码：1成功 2余额不足 3地区不在适用范围 4姓名手机号与注册不符 
	 *        5年龄/性别不符 6被保人未注册 0系统错误
	 */
	public String buyCommodity(MyUserInfo loginUser, My_order_info info) {
		
		//1、被保人是否注册过（按姓名+手机号查）
		MyUserInfo beneficiary = orderDao.selectUserByNameAndPhone(
				info.getOrder_beneficiary_name(), info.getOrder_beneficiary_phone());
		if(beneficiary == null) {
			//再按姓名查一次：姓名存在但手机号不对 → 提示4；姓名不存在 → 提示6
			MyUserInfo byName = orderDao.selectUserByName(info.getOrder_beneficiary_name());
			if(byName != null) {
				return "4";
			}
			return "6";
		}
		
		//2、拿商品信息（适用范围 + 年龄性别条件 + 价格基准）
		Commodity commodity = commodityDao.selectCommodityById(info.getCommodity_id());
		if(commodity == null) {
			return "0";
		}
		
		//3、被保人地区必须在商品适用范围内（とし输入值 vs commodity_applicable_location）
		String allow = commodity.getCommodity_applicable_location();
		if(allow == null || !allow.contains(info.getOrder_beneficiary_city())) {
			return "3";
		}
		
		//4、年龄校验（按被保人注册的生日算）
		int age = 0;
		if(beneficiary.getUser_birthday()!=null && beneficiary.getUser_birthday().length()>=4) {
			//年龄=获取的年份-生日年份，截取前4位
			age = Calendar.getInstance().get(Calendar.YEAR)
					- Integer.parseInt(beneficiary.getUser_birthday().substring(0, 4));
		}
		//判断是否在保险要求年龄范围内
		int ageStart = commodity.getCommodity_age_start()==null ? 0 : commodity.getCommodity_age_start();
		int ageEnd = commodity.getCommodity_age_end()==null ? 200 : commodity.getCommodity_age_end();
		if(!(age >= ageStart && age <= ageEnd)) {
			return "5";
		}
		
		//5、性别校验：1男 2女 3不限
		Integer cg = commodity.getCommodity_applicable_gender();
		int sex = "女".equals(beneficiary.getUser_sex()) ? 2 : 1;
		if(!(cg == null || cg == 3 || cg == sex)) {
			return "5";
		}
		
		//6、余额校验
		int money = 0;
		try {
			money = Integer.parseInt(loginUser.getUser_money()==null ? "0" : loginUser.getUser_money());
		} catch (NumberFormatException e) {
			money = 0;
		}
		//类型转换：把字符串形式的订单总价先解析成 double，再强制转换成 int，小数部分直接丢掉。
		int price = (int) Double.parseDouble(info.getOrder_sum_price());
		//余额不足提醒充钱
		if(money < price) {
			return "2";
		}
		
		//7、生成订单号（时间戳），补全起止时间
		String orderId = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		info.setOrder_id(orderId);
		info.setUser_id(loginUser.getUser_id());
		//默认有效期：起止年份
		int years = 1;
		try {
			 // 获取商品时长字符串，并解析为整数
			int t = Integer.parseInt(info.getOrder_commodity_time_length());
			// 如果时长为 0，则按 1 年处理；否则使用传入的时长
			years = (t == 0 ? 1 : t);
		} catch (NumberFormatException e) {
			years = 1;
		}
		// 获取起始年份（字符串转整数，例如 "2024" -> 2024）
		int startYear = Integer.parseInt(info.getOrder_commodity_time_start());
		info.setOrder_commodity_time_start(startYear + "年");
		info.setOrder_commodity_time_end((startYear + years) + "年");
		// 将有效年限重新写回为字符串，例如 "1"
		info.setOrder_commodity_time_length(years + "");
		
		//8、两张表插入 + 扣余额
		boolean r1 = orderDao.insertMyOrder(orderId, loginUser.getUser_id(), info.getCommodity_id());
		if(!r1) {
			return "0";
		}
		boolean r2 = orderDao.insertMyOrderInfo(info);
		if(!r2) {
			return "0";
		}
		boolean r3 = orderDao.updateUserMoney(loginUser.getUser_id(), String.valueOf(money - price));
		if(!r3) {
			return "0";
		}
		
		//9、把新余额回写登录对象（Servlet再同步到session）
		loginUser.setUser_money(String.valueOf(money - price));
		return "1";
	}
	
	/**
	 * 3、按用户ID查订单列表
	 */
	public List<My_order_info> selectOrderByUserId(Integer user_id) {
		return orderDao.selectOrderByUserId(user_id);
	}
}
