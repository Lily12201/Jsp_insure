package com.seiryo.service;

import java.util.List;

import com.seiryo.entity.AdminCommodity;

/**
 * 商品管理数据层
 * @ClassName: AdminCommodityService  
 * @Description: 1 成功 / 0 数据库出错 / 2 输入不合法 / 3 被别的表引用、不能删  
 * @author 李 
 * @date 2026年10月22日  
 *
 */
public interface AdminCommodityService {

    // 商品列表（keyword 输数字按ID查、输文字按名称模糊查；type 可以为空
    List<AdminCommodity> getCommodityList(String keyword, String type);

    //查单个商品 
    AdminCommodity getCommodityById(String commodityId);

    //新增商品
    String addCommodity(String name, String price, String type, String img,
                        String introduce, String timeLength, String ageStart, String ageEnd);

    //修改商品（commodityId 为空就是新增，不为空就是修改）
    String updateCommodity(String commodityId, String name, String price, String type,
                           String img, String introduce, String timeLength,
                           String ageStart, String ageEnd);

    //上架 / 下架（status：1 上架 / 0 下架）
    String changeStatus(String commodityId, String status);

    //删除商品（已经被人买过的商品不让删，只能下架）
    String deleteCommodity(String commodityId);
}
