package com.seiryo.dao;

import java.util.List;

import com.seiryo.entity.AdminCommodity;


/**
 * 对应需求：新增 / 删除 / 修改商品信息 / 上下架
 * @ClassName: AdminCommodityDao  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 李 
 * @date 2026年10月22日  
 *
 */
public interface AdminCommodityDao {

    /**
     * 商品列表。
     * keyword 为空 → 查全部
     * keyword 是纯数字 → 按 commodity_id 精确查（同时也会按商品名模糊查）
     * keyword 是文字   → 按商品名模糊查
     * type 不为空 → 再按商品类型筛一层
     */
    List<AdminCommodity> selectCommodityList(String keyword, String type);

    //根据 id 查一个商品（修改前先查出来填进表单）
    AdminCommodity selectCommodityById(int commodityId);

    //新增商品，返回 true 表示成功
    boolean insertCommodity(AdminCommodity c);

    //修改商品（名称 / 价格 / 类型 / 图片 / 介绍 / 期间 / 适用年龄）
    boolean updateCommodity(AdminCommodity c);

    //上架 / 下架：只改 commodity_status（1 上架 / 0 下架） 
    boolean updateCommodityStatus(int commodityId, int status);

    //删除商品 
    boolean deleteCommodity(int commodityId);

    //查这个商品有没有被买过（买过就不让删，只能下架）
    int countOrderByCommodityId(int commodityId);
}
