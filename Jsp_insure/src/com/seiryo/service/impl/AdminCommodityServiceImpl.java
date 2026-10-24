package com.seiryo.service.impl;

import java.util.List;

import com.seiryo.dao.AdminCommodityDao;
import com.seiryo.dao.impl.AdminCommodityDaoImpl;
import com.seiryo.entity.AdminCommodity;
import com.seiryo.service.AdminCommodityService;

/**
 *   Servlet 传来的全是字符串，Service 负责：
 *     1、非空判断（商品名不能空）
 *     2、格式判断（价格必须是数字且大于 0；类型必须是 1～5）
 *     3、把字符串转成数字
 *     4、调 Dao 执行 SQL，把 true/false 翻译成 "1"/"0"
 */
public class AdminCommodityServiceImpl implements AdminCommodityService {

    private AdminCommodityDao adminCommodityDao = new AdminCommodityDaoImpl();
/**
 * 1、查看商品列表（和查看用户列表一样）
 */
    public List<AdminCommodity> getCommodityList(String keyword, String type) {
        return adminCommodityDao.selectCommodityList(keyword, type);
    }
/**
 * 2、查看单个商品
 */
    public AdminCommodity getCommodityById(String commodityId) {
        int id = toInt(commodityId);
        if (id <= 0) {
            return null;
        }
        return adminCommodityDao.selectCommodityById(id);
    }
/**
 * 3、新增商品
 */
    public String addCommodity(String name, String price, String type, String img,
                               String introduce, String timeLength,
                               String ageStart, String ageEnd) {

        // 1、装对象（顺手做校验，不合格就返回 "2"）
        AdminCommodity c = packCommodity(null, name, price, type, img,
                                         introduce, timeLength, ageStart, ageEnd);
        if (c == null) {
            return "2";
        }

        // 2、新增的商品默认是「上架」状态
        c.setCommodity_status(1);

        boolean ok = adminCommodityDao.insertCommodity(c);
        return ok ? "1" : "0";
    }
/**
 * 4、更新商品
 */
    public String updateCommodity(String commodityId, String name, String price, String type,
                                  String img, String introduce, String timeLength,
                                  String ageStart, String ageEnd) {

        int id = toInt(commodityId);
        if (id <= 0) {
            return "2";
        }

        AdminCommodity c = packCommodity(id, name, price, type, img,
                                         introduce, timeLength, ageStart, ageEnd);
        if (c == null) {
            return "2";
        }

        boolean ok = adminCommodityDao.updateCommodity(c);
        return ok ? "1" : "0";
    }
/**
 * 5、改变商品状态：上架/下架
 */
    public String changeStatus(String commodityId, String status) {
        int id = toInt(commodityId);
        int st = toInt(status);
        if (id <= 0) {
            return "2";
        }
        // 状态只能是 0（下架）或 1（上架）
        if (st != 0 && st != 1) {
            return "2";
        }
        boolean ok = adminCommodityDao.updateCommodityStatus(id, st);
        return ok ? "1" : "0";
    }
/*
 * 6、删除商品
 */
    public String deleteCommodity(String commodityId) {
        int id = toInt(commodityId);
        if (id <= 0) {
            return "2";
        }

        // 已经被人买过的商品不能删：
        // 删掉的话，订单列表页 JOIN 商品表时就查不到商品名了，历史订单会变成空
        int orderCount = adminCommodityDao.countOrderByCommodityId(id);
        if (orderCount > 0) {
            return "3";     // 前端提示：请先下架
        }

        boolean ok = adminCommodityDao.deleteCommodity(id);
        return ok ? "1" : "0";
    }

    // ==================== 下面两个是工具方法 ====================

    /**
     * 这是一个"校验 + 装箱"的合体工具：把表单传来的 9 个字符串，检查一遍合不合法，
     * 合格就装成一个 AdminCommodity 对象返回，不合格就返回 null。
     */
    private AdminCommodity packCommodity(Integer id, String name, String price, String type,
                                         String img, String introduce, String timeLength,
                                         String ageStart, String ageEnd) {

        // 商品名必填
        if (name == null || name.trim().equals("")) {
            return null;
        }

        // 价格必须是数字，而且要大于 0
        if (price == null || price.trim().equals("")) {
            return null;
        }
        double p = 0;
        try {
            p = Double.parseDouble(price.trim());
        } catch (Exception e) {
            return null;
        }
        if (p <= 0) {
            return null;
        }

        // 商品类型必须是 1～5（和首页分类保持一致）
        // toInt() 是本文件最下面自己写的小工具：转得出来就返回数字，转不出来返回 -1
        int t = toInt(type);
        if (t < 1 || t > 5) {
            return null;
        }

        // 投保年龄：可以不填，不填就 0～200（等于不限制）
        int as = 0;
        int ae = 200;
        if (ageStart != null && !ageStart.trim().equals("")) {
            as = toInt(ageStart);
            if (as < 0) {
                return null;
            }
        }
        if (ageEnd != null && !ageEnd.trim().equals("")) {
            ae = toInt(ageEnd);
            if (ae < 0) {
                return null;
            }
        }
        if (as > ae) {
        	// 开始年龄比结束年龄还大，不合法
            return null;    
        }
        
        //新建一个AdminCommodity实体对象，把刚才校验好的各个值放进这个对象的属性里，
        //最后把这个装好的对象返回给调用方（Service/Servlet）。
      
        AdminCommodity c = new AdminCommodity();
        if (id != null) {
            c.setCommodity_id(id);
        }
        c.setCommodity_name(name.trim());
        c.setCommodity_price(price.trim());
        c.setCommodity_type(type.trim());
        c.setCommodity_img(img == null ? "" : img.trim());
        c.setCommodity_introduce(introduce == null ? "" : introduce.trim());
        c.setCommodity_time_length(timeLength == null ? "" : timeLength.trim());
        c.setCommodity_age_start(as);
        c.setCommodity_age_end(ae);
        return c;
    }

   //字符串转 int，转失败返回 -1（比抛异常省事） 
    private int toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }
}
