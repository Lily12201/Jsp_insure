package com.seiryo.dao;

import java.util.List;

import com.seiryo.entity.Commodity;
import com.seiryo.entity.Commodity_liability_info;

/**
 * 保险商品数据方法类
 * @ClassName: CommodityDao  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 李 
 * @date 2026年10月15日  
 *
 */
public interface CommodityDao {
	
	//主页展示使用
	//1、查询所有保险商品（首页未登录时遍历）
	List<Commodity>selectAllCommodityList();
	
	//2、按照商品类型查询（首页的分类1健康2意外3企业4旅行5家计）
	List<Commodity>selectCommodityListByType(Integer commodity_type);
	
	
	//3、根据商品ID查询商品（详情页面的头部信息）
	Commodity selectCommodityById(Integer commodity_id);
	
	// 根据商品id查询商品+保险责任（page_info.jsp / shop.jsp 用）
	Commodity_liability_info selectCommodityLiabilityInfoById(Integer commodity_id);
	

}
