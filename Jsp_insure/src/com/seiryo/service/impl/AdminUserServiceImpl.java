package com.seiryo.service.impl;

import java.util.List;

import com.seiryo.dao.AdminUserDao;
import com.seiryo.dao.impl.AdminUserDaoImpl;
import com.seiryo.service.AdminUserService;
import com.seiryo.entity.MyUserInfo;

/**
 *   Servlet 传来的都是字符串，Service 负责：
 *     1、把字符串转成数字（转失败就返回 "2"）
 *     2、检查数字合不合法（状态只能 0/1，充值金额必须大于 0）
 *     3、调 Dao 执行 SQL
 *     4、把结果翻译成 "1" / "0"
 */
public class AdminUserServiceImpl implements AdminUserService {

    private AdminUserDao adminUserDao = new AdminUserDaoImpl();
/**
 * 1、查询用户列表
 */
    public List<MyUserInfo> getUserList(String keyword) {
        return adminUserDao.selectUserList(keyword);
    }
/**
 * 2、修改用户状态
 */
    public String changeStatus(String userInfoId, String status) {
        // 1、把字符串转成 int，转不了就返回错误码 2
        int id = toInt(userInfoId);
        int st = toInt(status);
        if (id <= 0) {
            return "2";
        }
        // 2、状态只能是 0 或 1
        if (st != 0 && st != 1) {
            return "2";
        }
        // 3、执行
        boolean ok = adminUserDao.updateUserStatus(id, st);
        return ok ? "1" : "0";
    }
/*
 * 3、给用户充值
 */
    public String recharge(String userInfoId, String addMoney) {
        int id = toInt(userInfoId);
        int add = toInt(addMoney);
        if (id <= 0) {
            return "2";
        }
        // 充值金额必须是正数
        if (add <= 0) {
            return "2";
        }

        // 充值 = 原余额 + 充值金额，所以要先查出原来的余额
        // 查数据库是 Dao 的活，这里只管调它、判断结果
        int oldMoney = adminUserDao.selectMoney(id);
        if (oldMoney < 0) {
        	// 没查到这个用户
            return "0";   
        }

        boolean ok = adminUserDao.updateUserMoney(id, String.valueOf(oldMoney + add));
        return ok ? "1" : "0";
    }
/**
 * 4、删除用户
 */
    public String deleteUser(String userId, String userInfoId) {
        int uid = toInt(userId);
        int iid = toInt(userInfoId);
        if (uid <= 0 || iid <= 0) {
            return "2";
        }
        boolean ok = adminUserDao.deleteUser(uid, iid);
        return ok ? "1" : "0";
    }

    //小工具（其实不转换的话也行，js层直接接收字符串）if (date == "1")

    /** 字符串转 int，转失败返回 -1（比抛异常省事） */
    private int toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }
}
