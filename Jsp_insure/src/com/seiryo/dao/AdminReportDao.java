package com.seiryo.dao;
/**
 * 报表数据层接口
 * @ClassName: AdminReportDao  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 李 
 * @date 2026年10月23日  
 *
 */

import java.util.List;

import com.seiryo.entity.Statltem;

public interface AdminReportDao {
	
    //1、性别分布（统计下过单的会员）
	List<Statltem>selectSexStat();
	//2、按年龄分布
	List<Statltem>selectAgeStat();
	//3、按投保国家分布
	List<Statltem>selectCityStat();
	//4、按商品类型分布（销量+销售额）
	List<Statltem>selectTypeStat();
	//5、按月销售额
	List<Statltem>selectMonthStat();
	
}
