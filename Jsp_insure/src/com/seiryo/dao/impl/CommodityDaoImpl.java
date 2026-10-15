package com.seiryo.dao.impl;

import java.sql.ResultSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.seiryo.dao.CommodityDao;
import com.seiryo.entity.Commodity;
import com.seiryo.entity.Commodity_liability_info;
import com.seiryo.entity.MyUserInfo;
import com.seiryo.utile.JDBCUTILE;
/**
 * 保险商品数据库连接实现类
 * @ClassName: CommodityDaoImpl  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 李 
 * @date 2026年10月15日  
 *
 */
public class CommodityDaoImpl implements CommodityDao{
/*
 * 1、查询所有商品信息	
 */
	public List<Commodity>selectAllCommodityList(){
		String sql="SELECT commodity_id,commodity_name,commodity_price,commodity_introduce,commodity_img,"
				+ "commodity_age_start,commodity_age_end,commodity_time_length,commodity_applicable_gender,"
				+ "commodity_applicable_location,commodity_signing_form,commodity_liability_zjs,"
				+ "commodity_detailed_information1,commodity_detailed_information2,"
				+ "commodity_detailed_information3,commodity_type FROM commodity";
		List<Commodity>list=new ArrayList<Commodity>();
		ResultSet rest = null;
		try {
			rest=JDBCUTILE.selectDataBaseTable(sql);
		
			while (rest.next()) {
				// 关键：必须是 new
				Commodity commodity = new Commodity(
						rest.getInt("commodity_id"),
						rest.getString("commodity_name"),
						rest.getString("commodity_price"),
						rest.getString("commodity_introduce"),
						rest.getString("commodity_img"),
						rest.getInt("commodity_age_start"),
						rest.getInt("commodity_age_end"),
						rest.getString("commodity_time_length"),
						rest.getInt("commodity_applicable_gender"),
						rest.getString("commodity_applicable_location"),
						rest.getString("commodity_signing_form"),
						rest.getString("commodity_liability_zjs"),
						rest.getString("commodity_detailed_information1"),
						rest.getString("commodity_detailed_information2"),
						rest.getString("commodity_detailed_information3"),
						rest.getString("commodity_type"));
				list.add(commodity);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {try {
			JDBCUTILE.dbClose();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		}
		return list;
	}
/*
 * 	2、按照商品类型查询（首页的分类1健康2意外3企业4旅行5家计）
 */
	public List<Commodity>selectCommodityListByType(Integer commodity_type){
		String sql="SELECT commodity_id,commodity_name,commodity_price,commodity_introduce,commodity_img,"
				+ "commodity_age_start,commodity_age_end,commodity_time_length,commodity_applicable_gender,"
				+ "commodity_applicable_location,commodity_signing_form,commodity_liability_zjs,"
				+ "commodity_detailed_information1,commodity_detailed_information2,"
				+ "commodity_detailed_information3,commodity_type FROM commodity WHERE commodity_type= ? ";
		List<Commodity>list=new ArrayList<Commodity>();
		ResultSet rest = null;
		try {
			rest=JDBCUTILE.selectDataBaseTable(sql, commodity_type);
			while(rest.next()) {
				Commodity commodity = new Commodity(
						rest.getInt("commodity_id"),
						rest.getString("commodity_name"),
						rest.getString("commodity_price"),
						rest.getString("commodity_introduce"),
						rest.getString("commodity_img"),
						rest.getInt("commodity_age_start"),
						rest.getInt("commodity_age_end"),
						rest.getString("commodity_time_length"),
						rest.getInt("commodity_applicable_gender"),
						rest.getString("commodity_applicable_location"),
						rest.getString("commodity_signing_form"),
						rest.getString("commodity_liability_zjs"),
						rest.getString("commodity_detailed_information1"),
						rest.getString("commodity_detailed_information2"),
						rest.getString("commodity_detailed_information3"),
						rest.getString("commodity_type"));
				list.add(commodity);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}finally {
			try {
				JDBCUTILE.dbClose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
/*
 * 3、根据商品ID查询商品
 */
	
	public	Commodity selectCommodityById(Integer commodity_id)	{
		String sql="SELECT commodity_id,commodity_name,commodity_price,commodity_introduce,commodity_img,"
				+ "commodity_age_start,commodity_age_end,commodity_time_length,commodity_applicable_gender,"
				+ "commodity_applicable_location,commodity_signing_form,commodity_liability_zjs,"
				+ "commodity_detailed_information1,commodity_detailed_information2,"
				+ "commodity_detailed_information3,commodity_type FROM commodity WHERE commodity_id= ? ";
		Commodity commodity = null; 
		ResultSet rest = null;
		try {
			rest=JDBCUTILE.selectDataBaseTable(sql, commodity_id);
			if(rest.next()) {
				commodity = new Commodity(   
						rest.getInt("commodity_id"),
						rest.getString("commodity_name"),
						rest.getString("commodity_price"),
						rest.getString("commodity_introduce"),
						rest.getString("commodity_img"),
						rest.getInt("commodity_age_start"),
						rest.getInt("commodity_age_end"),
						rest.getString("commodity_time_length"),
						rest.getInt("commodity_applicable_gender"),
						rest.getString("commodity_applicable_location"),
						rest.getString("commodity_signing_form"),
						rest.getString("commodity_liability_zjs"),
						rest.getString("commodity_detailed_information1"),
						rest.getString("commodity_detailed_information2"),
						rest.getString("commodity_detailed_information3"),
						rest.getString("commodity_type"));
				
			}
		} catch (Exception e) {

			e.printStackTrace();
		}finally {
			try {
				JDBCUTILE.dbClose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return commodity;
	}
	
	/*
	 * 4、根据商品ID查询商品+保险责任（page_info.jsp / shop.jsp 用）
	 */
	@Override
	public Commodity_liability_info selectCommodityLiabilityInfoById(Integer commodity_id) {

		String sql = "SELECT c.commodity_id,c.commodity_name,c.commodity_price,c.commodity_introduce,c.commodity_img,"
				+ "c.commodity_age_start,c.commodity_age_end,c.commodity_time_length,c.commodity_applicable_gender,"
				+ "c.commodity_applicable_location,c.commodity_signing_form,c.commodity_liability_zjs,"
				+ "c.commodity_detailed_information1,c.commodity_detailed_information2,"
				+ "c.commodity_detailed_information3,c.commodity_type,"
				+ "l.commodity_liability_info_id,l.commodity_liability_info1,l.commodity_liability_info2,"
				+ "l.commodity_liability_info3,l.commodity_liability_info4,l.commodity_liability_info5,"
				+ "l.commodity_liability_info6,l.commodity_liability_info7 "
				+ "FROM commodity c LEFT JOIN commodity_liability_info l "
				+ "ON c.commodity_id = l.commodity_id WHERE c.commodity_id = ?";

		Commodity_liability_info info = null;
		ResultSet rest = null;
		try {
			rest = JDBCUTILE.selectDataBaseTable(sql, commodity_id);
			if (rest.next()) {
				// 24 个参数：前 16 个是父类 commodity 的字段，后 8 个是责任字段
				info = new Commodity_liability_info(
						rest.getInt("commodity_id"),
						rest.getString("commodity_name"),
						rest.getString("commodity_price"),
						rest.getString("commodity_introduce"),
						rest.getString("commodity_img"),
						rest.getInt("commodity_age_start"),
						rest.getInt("commodity_age_end"),
						rest.getString("commodity_time_length"),
						rest.getInt("commodity_applicable_gender"),
						rest.getString("commodity_applicable_location"),
						rest.getString("commodity_signing_form"),
						rest.getString("commodity_liability_zjs"),
						rest.getString("commodity_detailed_information1"),
						rest.getString("commodity_detailed_information2"),
						rest.getString("commodity_detailed_information3"),
						rest.getString("commodity_type"),
						rest.getInt("commodity_liability_info_id"),
						rest.getString("commodity_liability_info1"),
						rest.getString("commodity_liability_info2"),
						rest.getString("commodity_liability_info3"),
						rest.getString("commodity_liability_info4"),
						rest.getString("commodity_liability_info5"),
						rest.getString("commodity_liability_info6"),
						rest.getString("commodity_liability_info7"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				JDBCUTILE.dbClose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return info;
	}
	
	
	
}
