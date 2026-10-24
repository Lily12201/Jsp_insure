package com.seiryo.dao;

import java.util.List;

import com.seiryo.entity.My_order_info;

/**
 * 订单管理数据层
 * @ClassName: AdminOrderDao  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 李 
 * @date 2026年10月23日  
 *   用的实体：直接复用前台的 My_order_info（com.seiryo.entity.My_order_info）
 *   它 extends My_order，所以自带 order_id / user_id / commodity_id；
 *   自己还有被保人信息、金额、保险期间、7 个保障金额；
 *   而且已经预留了 commodity_name 和 user_name 两个「联查专用」字段，
 */
public interface AdminOrderDao {

    /**
     * 1、订单一览表
     * @Title: selectOrderList
     * @Description: 三个条件都可以为空，为空就表示：不按照条件查，显示全部
     * @param orderId         订单号（模糊查）
     * @param beneficiaryName 被保人姓名（模糊查）
     * @param phone           手机号（模糊查）
     * @return List<My_order_info> 订单集合（按订单号倒序，最新在前）
     */
    List<My_order_info> selectOrderList(String orderId, String beneficiaryName, String phone);

    /**
     * 2、删除订单
     * @Title: deleteOrder
     * @Description: 订单是「主表 my_order + 明细表 my_order_info」两张表，
     *               必须【先删明细，再删主表】，顺序反了会留下垃圾数据
     * @param orderId 订单号
     * @return boolean true=删除成功
     */
    boolean deleteOrder(String orderId);

    /**
     * 3、总订单数（页面顶部「総注文数」用）
     * @return int 订单条数
     */
    int countOrder();

    /**
     *4、 销售总额（页面顶部「売上合計」用）
     * @return String 金额（用 String 是因为 Dao 层不做格式化，直接把数据库算出来的原样返回）
     */
    String sumOrderPrice();
}
