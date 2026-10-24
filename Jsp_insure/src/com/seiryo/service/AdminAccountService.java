package com.seiryo.service;

import java.util.List;

import com.seiryo.entity.AdminUser;

/**
 * 返回值约定：
 *   "1" 成功
 *   "2" 输入的值不合法（账号 3~20 位、密码 6~20 位）
 *   "3" 账号已经被占用了 ／ 新旧密码一样（changeOwnPassword 用）
 *   "4" 権限がありません（普通管理员想动别人）
 *   "5" 自分自身は操作できません（请用下面的「パスワード変更」）
 *   "6" 最後の総管理者は変更できません（至少留 1 个总管理员）
 *   "0" 失败（SQL 出错 ／ 旧密码不对）
 */
public interface AdminAccountService {

   
    //1、查管理员列表：当前登录者的角色（1 总管理员看全部，2 只看自己）当前登录者的 id
     
    List<AdminUser> getAccountList(int myRole, int myId);

     //新增管理员（只有总管理员能用）。role：1 总管理员 / 2 普通 
    String addAccount(String account, String password, String name, String role, int myRole);

    //启用 / 停用（只有总管理员能用）
    String changeStatus(String adminId, String status, int myRole, int myId);

    //重设「别人」的密码（只有总管理员能用）
    String resetPassword(String adminId, String password, int myRole, int myId);

    //升降级（只有总管理员能用）
    String changeRole(String adminId, String role, int myRole, int myId);

    //删除账号（只有总管理员能用
    String deleteAccount(String adminId, int myRole, int myId);

     /**
     * 改「自己」的密码 —— 所有人都能用，但必须输对旧密码
     * 注意和 resetPassword 的区别：
     *   改自己 → 要旧密码（防止离开座位被人改掉）
     *   重置别人 → 不要旧密码（管理行为，总管理员专用）
     */
    String changeOwnPassword(int myId, String oldPassword, String newPassword);
}
