package com.seiryo.service;

import java.util.List;

import com.seiryo.entity.My_order_info;


/**
 * 订单管理 Service 接口（后台）
 * @ClassName: AdminOrderService  
 * @Description: 1成功；2输入不合法（订单号为空）；0数据库出错 
 * @author 李 
 * @date 2026年10月23日  
 *
 */
public interface AdminOrderService {

    //订单一览表（三个查询条件都可为空）
     
    List<My_order_info> getOrderList(String orderId, String beneficiaryName, String phone);

    
     //删除订单："1"成功 / "2"订单号为空 / "0"失败
  
     String deleteOrder(String orderId);

    //总订单数
    int countOrder();

    //销售总额
    String sumOrderPrice();
}
