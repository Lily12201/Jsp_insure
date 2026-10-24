package com.seiryo.dao.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.seiryo.dao.AdminReportDao;
import com.seiryo.entity.Statltem;
import com.seiryo.utile.JDBCUTILE;
/**
 * 报表数据层实现类
 * @ClassName: AdminReportDaoImpl  
 * @Description:1、需要用GROUP BY分组统计；2、需要排序显示 
 * @author 李 
 * @date 2026年10月23日  
 *
 */
public class AdminReportDaoImpl implements AdminReportDao{

	/**
	 * 1、报表1：性别分布
	 */
	public List<Statltem>selectSexStat(){
		//IFNULL:如果性别为空值就显示未设定
		String sql="SELECT IFNULL(u.user_sex,'未設定')AS label,COUNT(*)AS cnt FROM my_order o,my_user_info u "
				+ "WHERE o.user_id=u.user_id "
				+ "GROUP BY u.user_sex "
				+ "ORDER BY cnt DESC";
		//返回查的数据（创建一个方法调用）
		return queryTwoColumn(sql);
	}
 
	/**
	 * 2、报表2：年龄分布
	 */
	public List<Statltem>selectAgeStat(){
		//先用逻辑计算再查询
		//年龄=今年-出生年份
		//user_birthday截取前4位来减；并且把birthday的字符串转成数字才能做减法；用CAST(...AS UNSIGNED)
		//先内层算出每个人的年龄，外层再按年龄分组
		//CASE WHEN 就是 SQL 里的 if / else：
	    //   满足 WHEN 的条件，就取 THEN 后面的值，都不满足取 ELSE

		String sql = "SELECT CASE "
                + "         WHEN t.age < 18 THEN '0-17歳' "
                + "         WHEN t.age < 26 THEN '18-25歳' "
                + "         WHEN t.age < 36 THEN '26-35歳' "
                + "         WHEN t.age < 46 THEN '36-45歳' "
                + "         WHEN t.age < 61 THEN '46-60歳' "
                + "         ELSE '61歳以上' "
                + "       END AS label, "
                + "       COUNT(*) AS cnt "
                + "FROM ( "
                + "   SELECT YEAR(NOW()) - CAST(LEFT(u.user_birthday,4) AS UNSIGNED) AS age "
                + "   FROM my_order o, my_user_info u "
                + "   WHERE o.user_id = u.user_id "
                + "     AND u.user_birthday IS NOT NULL "
                + "     AND u.user_birthday <> '' "
                + ") t "
                + "GROUP BY label "
                + "ORDER BY label";
     return queryTwoColumn(sql);

				
		
	}

	/**
	 * 3、报表3：按照投保国家分布
	 */
	
	public List<Statltem>selectCityStat(){
		String sql="SELECT IFNULL(order_beneficiary_city,'未設定')AS label,COUNT(*)AS cnt "
				+ "FROM my_order_info "
				+ "GROUP BY order_beneficiary_city "
				+ "ORDER BY cnt DESC";
		return queryTwoColumn(sql);
	}
	
	/**
	 * 4、报表4：按照商品类型分布
	 */
	public List<Statltem>selectTypeStat(){
		// commodity_type 存的是 '1'~'5'，页面要显示中文，用 CASE 翻译
        // 1健康 2意外 3企业 4旅行 5家计
        //
        // CAST(order_sum_price AS DECIMAL(14,2))：
        //   这个字段在数据库里是 VARCHAR（文字），必须转成小数才能正确求和

		   String sql = "SELECT CASE c.commodity_type "
                   + "         WHEN '1' THEN '健康' WHEN '2' THEN '意外' "
                   + "         WHEN '3' THEN '企業' WHEN '4' THEN '旅行' "
                   + "         WHEN '5' THEN '家計' ELSE 'その他' "
                   + "       END AS label, "
                   + "       COUNT(*) AS cnt, "
                   + "       IFNULL(SUM(CAST(oi.order_sum_price AS DECIMAL(14,2))),0) AS amount "
                   + "FROM my_order o, my_order_info oi, commodity c "
                   + "WHERE o.order_id = oi.order_id "
                   + "  AND o.commodity_id = c.commodity_id "
                   + "GROUP BY c.commodity_type "
                   + "ORDER BY amount DESC";
        return queryThreeColumn(sql);

	}
	
	/**
	 * 5、报表5：月度销售额趋势
	 */
    public List<Statltem> selectMonthStat() {
        // order_id 是 "20261020130000" 这种格式（年月日时分秒）
        //   STR_TO_DATE(order_id,'%Y%m%d%H%i%s')  → 变成真正的日期
        //   DATE_FORMAT(..., '%Y-%m')            → 取出 "2026-10"
        // 这样就不需要给 my_order 表另外加一个下单时间字段了
        String sql = "SELECT DATE_FORMAT(STR_TO_DATE(o.order_id,'%Y%m%d%H%i%s'), '%Y-%m') AS label, "
                   + "       COUNT(*) AS cnt, "
                   + "       IFNULL(SUM(CAST(oi.order_sum_price AS DECIMAL(14,2))),0) AS amount "
                   + "FROM my_order o, my_order_info oi "
                   + "WHERE o.order_id = oi.order_id "
                   + "GROUP BY label "
                   + "ORDER BY label";
        return queryThreeColumn(sql);
    }

	
	
/**
 * 6、1\2\3查询数据库使用：项目名+数量	（查两列）
 */

	private List<Statltem>queryTwoColumn(String sql){
		List<Statltem>list=new ArrayList<Statltem>();
		ResultSet rs=null;
		try {
			rs=JDBCUTILE.selectDataBaseTable(sql);
			while(rs.next()) {
				Statltem item=new Statltem();
				item.setLabel(rs.getString("label"));
				item.setValue(rs.getInt("cnt"));
				list.add(item);
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
		fillPercent(list);
		return list;
		
	}
  /**
   * 7、查三列：名称 + 数量 + 金额（报表 4、5 用） 
   */
    private List<Statltem> queryThreeColumn(String sql) {
        List<Statltem> list = new ArrayList<Statltem>();
        ResultSet rs = null;
        try {
            rs = JDBCUTILE.selectDataBaseTable(sql);
            while (rs.next()) {
                Statltem item = new Statltem();
                item.setLabel(rs.getString("label"));
                item.setValue(rs.getInt("cnt"));
                item.setAmount(rs.getDouble("amount"));
                list.add(item);
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
        fillPercent(list);
        return list;
    }

    /** 
     *8、 算每一行占总数的百分比（页面上画进度条要用） 
     * */
    private void fillPercent(List<Statltem> list) {
        int total = 0;
        for (Statltem item : list) {
            total = total + item.getValue();
        }
        if (total == 0) {
        	 // 一条数据都没有，除 0 会崩，直接返回
            return;   
        }
        for (Statltem item : list) {
            // ×1000 再 ÷10 是保留 1 位小数的写法：35.29 → 352.9 → 353 → 35.3
            item.setPercent(Math.round(item.getValue() * 1000.0 / total) / 10.0);
        }
    }
	
	
	
	
}
