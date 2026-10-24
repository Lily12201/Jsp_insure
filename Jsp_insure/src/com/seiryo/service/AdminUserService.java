package com.seiryo.service;

import java.util.List;

import com.seiryo.entity.MyUserInfo;

public interface AdminUserService {
    /**
     * 查用户列表。
     * keyword 是纯数字时按 ID 精确查，是文字时按姓名模糊查，空就查全部
     */
    List<MyUserInfo> getUserList(String  keyword);

    //禁用 / 启用。status 只能是 0 或 1
    String changeStatus(String userInfoId, String status);

    //充值。addMoney 要是正整数，会在原余额上加上去
    String recharge(String userInfoId, String addMoney);

    // 删除用户
    String deleteUser(String userId, String userInfoId);


}
