package com.seiryo.service;

import java.util.List;

import com.seiryo.entity.Commodity;
import com.seiryo.entity.Commodity_liability_info;
import com.seiryo.entity.MyUserInfo;

public interface CommodityService {
	
	List<Commodity> selectAllCommodityList();

	List<Commodity> selectCommodityListByType(Integer commodity_type);

	// 根据登录用户的年龄/性别推荐排序
	List<Commodity> selectCommodityListByUserInfo(MyUserInfo myUserInfo);

	Commodity selectCommodityById(Integer commodity_id);

	Commodity_liability_info selectCommodityLiabilityInfoById(Integer commodity_id);
	
	

}
