package com.seiryo.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.seiryo.dao.CommodityDao;
import com.seiryo.dao.impl.CommodityDaoImpl;
import com.seiryo.entity.Commodity;
import com.seiryo.entity.Commodity_liability_info;
import com.seiryo.entity.MyUserInfo;
import com.seiryo.service.CommodityService;

public class CommodityServiceImpl implements CommodityService{
	
	private CommodityDao commodityDao = new CommodityDaoImpl();

	@Override
	public List<Commodity> selectAllCommodityList() {
		return commodityDao.selectAllCommodityList();
	}

	@Override
	public List<Commodity> selectCommodityListByType(Integer commodity_type) {
		return commodityDao.selectCommodityListByType(commodity_type);
	}

	@Override
	public Commodity selectCommodityById(Integer commodity_id) {
		return commodityDao.selectCommodityById(commodity_id);
	}

	@Override
	public Commodity_liability_info selectCommodityLiabilityInfoById(Integer commodity_id) {
		return commodityDao.selectCommodityLiabilityInfoById(commodity_id);
	}

	/**
	 * 根据用户信息推荐排序
	 * 年龄+性别都符合 > 仅年龄符合 > 其余
	 */
	@Override
	public List<Commodity> selectCommodityListByUserInfo(MyUserInfo myUserInfo) {

		// 1、取全商品表全部数据
		List<Commodity> all = commodityDao.selectAllCommodityList();

		// 2、取出用户的年龄和性别数据
		int age = getUserAge(myUserInfo);
		// 1=男 2=女
		int sex = getUserSex(myUserInfo);   
		//3、创建3个集合来分类商品
		//年龄和性别全部匹配时
		List<Commodity> both = new ArrayList<Commodity>();
		//仅仅年龄匹配时
		List<Commodity> ageOnly = new ArrayList<Commodity>();
		//年龄不匹配时
		List<Commodity> other = new ArrayList<Commodity>();
		
		//4、循环遍历每一件商品，进行归类
		for (Commodity c : all) {
			//如果商品适用起始年龄是null，就给默认0；否则取本身的值
			int start = c.getCommodity_age_start() == null ? 0 : c.getCommodity_age_start();
			//如果商品适用结束年龄是null，就默认值200；否则取本身的值
			int end = c.getCommodity_age_end() == null ? 200 : c.getCommodity_age_end();
			//5、判断用年龄是否落在商品的年龄区间里面
			boolean ageFit = age >= start && age <= end;
			
			//获取商品使用性别：1男，2女，3不限性别
			Integer cg = c.getCommodity_applicable_gender();  
			//6、性别匹配条件
			//null:这个商品没填适用性别 → 不限制，谁都合适;3:不限性别；sex:商品的适用性别正好等于用户自己的性别
			boolean sexFit = (cg == null || cg == 3 || cg == sex);
			
			//7、判断分支
			if (ageFit && sexFit) {
				//年龄和性别符合放进both集合
				both.add(c);
			} else if (ageFit) {
				//年龄符合，但性别不符合，放进ageOnly集合
				ageOnly.add(c);
			} else {
				//年龄不匹配，放other集合
				other.add(c);
			}
		}
		
		//8、创建最终返回的列表，按照优先级别拼接
		List<Commodity> result = new ArrayList<Commodity>();
		//先放年龄+性别匹配的商品
		result.addAll(both);
		//再放“仅年龄匹配”的商品
		result.addAll(ageOnly);
		//最后放年龄不匹配的商品
		result.addAll(other);
		return result;
	}

	/**
	 * 由生日(yyyyMMdd)算年龄
	 */
	private int getUserAge(MyUserInfo myUserInfo) {
		try {
			//1、获取用户生日字符串
			String birthday = myUserInfo.getUser_birthday();
			//2、判断非空，并且长度至少4个字符以上
			if (birthday != null && birthday.length() >= 4) {
				//3、截取最前面的4位
				int birthYear = Integer.parseInt(birthday.substring(0, 4));
				//5、当前年份-出生年份=年龄
				return Calendar.getInstance().get(Calendar.YEAR) - birthYear;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/** 
	 * 性别映射：先生=1，女士=2
	 * 因为注册的时候存进库的是男和女；但是数据库显示存的是“先生”“女士”，所有要转换
	 *  */
	private int getUserSex(MyUserInfo myUserInfo) {
		String sex=myUserInfo.getUser_sex();
		if("先生".equals(sex)||"男".equals(sex)||"1".equals(sex)) {
			return 1;
		}else {
			return 2;
		}
	}
}
	
	


