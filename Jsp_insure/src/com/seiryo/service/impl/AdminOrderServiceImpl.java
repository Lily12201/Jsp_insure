package com.seiryo.service.impl;

import java.util.List;

import com.seiryo.dao.AdminOrderDao;
import com.seiryo.dao.impl.AdminOrderDaoImpl;
import com.seiryo.entity.My_order_info;
import com.seiryo.service.AdminOrderService;

/**
 * 订单Service层实现类
 * @ClassName: AdminOrderServiceImpl  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 李 
 * @date 2026年10月23日  
 *所以 Service 主要做两件事：
 *  判空（订单号是主键，空着不能删）
 *  把 Dao 返回的 boolean 翻译成 "1" / "0" 给 Servlet
 */
public class AdminOrderServiceImpl implements AdminOrderService {


    private AdminOrderDao adminOrderDao = new AdminOrderDaoImpl();

 
     //1、订单一览表：不做任何判断，直接把三个条件原样传给 Dao
    
    public List<My_order_info> getOrderList(String orderId, String beneficiaryName, String phone) {
        return adminOrderDao.selectOrderList(orderId, beneficiaryName, phone);
    }

   //2、删除订单
    public String deleteOrder(String orderId) {

        // 订单号是主键。如果不判空，SQL 会变成 WHERE order_id = '' ，
        // 所以必须在 Service 挡一道。
        if (orderId == null || orderId.trim().equals("")) {
        	// 输入不合法 → 前端弹「入力値が正しくありません」
            return "2";   
        }

        boolean ok = adminOrderDao.deleteOrder(orderId.trim());
        return ok ? "1" : "0";
    }
   //3、总订单数
    public int countOrder() {
        return adminOrderDao.countOrder();
    }

    //4、销售总额
    public String sumOrderPrice() {
        return adminOrderDao.sumOrderPrice();
    }
}
