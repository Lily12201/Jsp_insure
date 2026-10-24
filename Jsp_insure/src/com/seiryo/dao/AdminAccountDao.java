package com.seiryo.dao;

import java.util.List;

import com.seiryo.entity.AdminUser;

/**
 * 管理者管理Dao层
 * @ClassName: AdminAccountDao  
 * @Description: 操作的是 admin_user 表  
 * @author 李 
 * @date 2026年10月22日  
 *
 */
public interface AdminAccountDao {

	//查管理员列表。 myRole = 1（总管理员）查所有人；myRole = 2（普通）只查 myId 这一条
    
    List<AdminUser> selectAccountList(int myRole, int myId);

    //查某一个管理员（用来判断对方是不是总管理员、验证旧密码对不对）
    AdminUser selectById(int adminId);

    //数一数现在有几个「启用状态的总管理员」
    int countSuperAdmin();

    //查账号有没有被占用。返回 0 = 没被占用，可以用 
    int countByAccount(String account);

    //新增管理员（role 在 AdminUser 对象里）
    boolean insertAccount(AdminUser admin);

    //启用 / 禁用：改 admin_status 
    boolean updateStatus(int adminId, int status);

    //改密码（重置别人的、改自己的，都用它） 
    boolean updatePassword(int adminId, String password);

    //改角色：把某个账号升成总管理员 / 降成普通管理员 
    boolean updateRole(int adminId, int role);

    // 删除账号
    boolean deleteAccount(int adminId);
}
