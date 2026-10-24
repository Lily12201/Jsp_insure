package com.seiryo.entity;

import com.seiryo.entity.Commodity;

/**
 * 商品管理用的实体（后台）
 * 放这里：src/com/seiryo/entity/AdminCommodity.java
 *
 * 为什么不直接用 Commodity？
 *   因为要做「上架 / 下架」，需要多一个字段 commodity_status，
 *   而前台的 Commodity 里没有这个字段。
 *
 * 用 extends（继承）的好处：
 *   1、Commodity 原有的十几个 getter / setter 全部直接拿来用，一行不用重写
 *   2、完全不动你前台的 Commodity.java（改了怕影响前台页面）
 *  
 *
 * 状态：1 = 上架（販売中）  0 = 下架（販売停止）
 */
public class AdminCommodity extends Commodity {

    //上架状态：1 上架 / 0 下架
    private Integer commodity_status;

    //这个商品被买了多少单（列表页显示用，JOIN 出来的，不是表里的字段）
    private Integer order_count;

    public AdminCommodity() {
        super();
    }

    public Integer getCommodity_status() {
        return commodity_status;
    }

    public void setCommodity_status(Integer commodity_status) {
        this.commodity_status = commodity_status;
    }

    public Integer getOrder_count() {
        return order_count;
    }

    public void setOrder_count(Integer order_count) {
        this.order_count = order_count;
    }
}
