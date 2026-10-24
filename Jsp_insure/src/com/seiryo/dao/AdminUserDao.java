package com.seiryo.dao;

import java.util.List;

import com.seiryo.entity.MyUserInfo;
/**
 *  4 个方法：查列表、禁用/启用、充值、删除
 * @ClassName: AdminUserDao  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 李 
 * @date 2026年10月21日  
 *
 */
public interface AdminUserDao {
	
	 /**
     * 查用户列表。
     * keyword 为空 → 查全部
     * keyword 是纯数字 → 按 user_id 精确查（同时也会按姓名模糊查）
     * keyword 是文字   → 按姓名模糊查
     */
    List<MyUserInfo> selectUserList(String keyword);
    
    //查某个用户现在的余额。查不到这个人返回 -1，余额为空返回 0 
    int selectMoney(int userInfoId);

    //禁用 / 启用：改 my_user_info.user_status/
    boolean updateUserStatus(int userInfoId, int status);

   //充值：改 my_user_info.user_money（钱数由 Service 算好传进来）/
    boolean updateUserMoney(int userInfoId, String money);

    //删除：先删 my_user_info，再删 my_user/
    boolean deleteUser(int userId, int userInfoId);

 

}
