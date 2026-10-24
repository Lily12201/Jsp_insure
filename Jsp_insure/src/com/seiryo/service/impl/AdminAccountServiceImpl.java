package com.seiryo.service.impl;

import java.util.List;

import com.seiryo.dao.AdminAccountDao;
import com.seiryo.dao.impl.AdminAccountDaoImpl;
import com.seiryo.entity.AdminUser;
import com.seiryo.service.AdminAccountService;

/**
 * Service 负责（业务逻辑）：
 *   1、检查「有没有权限」（总管理员才能管别人）
 *   2、检查「是不是在动自己」（不允许）
 *   3、检查「是不是最后一个总管理员」（至少留 1 个）
 *   4、检查输入合不合法、查重
 *   5、调 Dao，翻译成返回码
 *
 * 返回码：1成功 2输入不合法 3账号已存在 4権限なし 5自分は不可 6最後の総管理者 0失败
 */
public class AdminAccountServiceImpl implements AdminAccountService {

    private AdminAccountDao adminAccountDao = new AdminAccountDaoImpl();

    public List<AdminUser> getAccountList(int myRole, int myId) {
        return adminAccountDao.selectAccountList(myRole, myId);
    }
/**
 * 1、新增管理员
 */
    public String addAccount(String account, String password, String name, String role, int myRole) {

        // 0、只有总管理员能开新账号
        if (myRole != 1) {
            return "4";
        }

        // 1、账号不能为空，长度 3~20
        if (account == null || account.trim().equals("")
                || account.trim().length() < 3 || account.trim().length() > 20) {
            return "2";
        }
        // 2、密码不能为空，长度 6~20
        if (password == null || password.trim().equals("")
                || password.trim().length() < 6 || password.trim().length() > 20) {
            return "2";
        }
        // 3、姓名可以为空
        if (name == null || name.trim().equals("")) {
            name = "（未設定）";
        }
        // 4、角色只能是 1 或 2，没传就当普通管理员
        int r = toInt(role);
        if (r != 1 && r != 2) {
            r = 2;
        }
        // 5、查重
        if (adminAccountDao.countByAccount(account.trim()) > 0) {
            return "3";
        }

        // 6、新增，状态默认 1（启用）
        AdminUser admin = new AdminUser();
        admin.setAdmin_account(account.trim());
        admin.setAdmin_password(password.trim());
        admin.setAdmin_name(name.trim());
        admin.setAdmin_role(r);
        admin.setAdmin_status(1);

        boolean ok = adminAccountDao.insertAccount(admin);
        return ok ? "1" : "0";
    }
/**
 * 2、启用 / 停用
 */
    public String changeStatus(String adminId, String status, int myRole, int myId) {

        if (myRole != 1) {
            return "4";
        }
        int id = toInt(adminId);
        int st = toInt(status);
        if (id <= 0) {
            return "2";
        }
        if (st != 0 && st != 1) {
            return "2";
        }
        // 不能停用自己
        if (id == myId) {
            return "5";
        }
        // 停用前先看：这是不是最后一个总管理员
        if (st == 0 && isLastSuperAdmin(id)) {
            return "6";
        }

        boolean ok = adminAccountDao.updateStatus(id, st);
        return ok ? "1" : "0";
    }

   /**
    *3、 重设「别人」的密码
    */
    public String resetPassword(String adminId, String password, int myRole, int myId) {

        if (myRole != 1) {
            return "4";
        }
        int id = toInt(adminId);
        if (id <= 0) {
            return "2";
        }
        if (password == null || password.trim().length() < 6
                || password.trim().length() > 20) {
            return "2";
        }
        // 改自己的密码请走 changeOwnPassword（那个要输旧密码，更安全）
        if (id == myId) {
            return "5";
        }

        boolean ok = adminAccountDao.updatePassword(id, password.trim());
        return ok ? "1" : "0";
    }
/**
 * 4、升降级
 */
    public String changeRole(String adminId, String role, int myRole, int myId) {

        if (myRole != 1) {
            return "4";
        }
        int id = toInt(adminId);
        int r  = toInt(role);
        if (id <= 0) {
            return "2";
        }
        if (r != 1 && r != 2) {
            return "2";
        }
        // 不能改自己的角色（否则能把自己升上去，也能把自己降下来）
        if (id == myId) {
            return "5";
        }
        // 不能把最后一个总管理员降成普通
        if (r == 2 && isLastSuperAdmin(id)) {
            return "6";
        }

        boolean ok = adminAccountDao.updateRole(id, r);
        return ok ? "1" : "0";
    }
/**
 * 5、删除账号
 */
    public String deleteAccount(String adminId, int myRole, int myId) {

        if (myRole != 1) {
            return "4";
        }
        int id = toInt(adminId);
        if (id <= 0) {
            return "2";
        }
        // 不能删自己
        if (id == myId) {
            return "5";
        }
        // 不能删最后一个总管理员
        if (isLastSuperAdmin(id)) {
            return "6";
        }

        boolean ok = adminAccountDao.deleteAccount(id);
        return ok ? "1" : "0";
    }
/**
 * 6、改「自己」的密码
 */
    public String changeOwnPassword(int myId, String oldPassword, String newPassword) {

        // 1、新密码长度 6~20
        if (newPassword == null || newPassword.trim().length() < 6
                || newPassword.trim().length() > 20) {
            return "2";
        }
        // 2、旧密码不能空
        if (oldPassword == null || oldPassword.trim().equals("")) {
            return "2";
        }
        // 3、新密码不能和旧密码一样（改了等于没改）
        //   返回 3，JS 那边会显示「現在のパスワードと同じです」
        if (oldPassword.trim().equals(newPassword.trim())) {
            return "3";
        }

        // 4、先查出自己，比对旧密码对不对
        AdminUser me = adminAccountDao.selectById(myId);
        if (me == null) {
            return "0";
        }
        // 返回 0，JS 那边会显示「現在のパスワードが違います」。
        //   这里不能返回 2，否则用户会看到「6文字以上で」这种不相干的提示。
        if (!oldPassword.trim().equals(me.getAdmin_password())) {
            return "0";
        }

        // 5、旧密码对了才改
        boolean ok = adminAccountDao.updatePassword(myId, newPassword.trim());
        return ok ? "1" : "0";
    }

    // ==================== 下面是两个小工具方法 ====================

    /**
     * 判断「adminId 这个人是不是最后一个还在用的总管理员」
     * 判断方法：先数现在有几个有效总管理员，再看这人是不是其中之一
     */
    private boolean isLastSuperAdmin(int adminId) {
        int cnt = adminAccountDao.countSuperAdmin();
        if (cnt > 1) {
        	// 还有别人，删了这个也不影响
            return false;            
        }
        AdminUser target = adminAccountDao.selectById(adminId);
        if (target == null) {
            return false;
        }
        Integer role = target.getAdmin_role();
        return role != null && role.intValue() == 1;
    }

    /** 字符串转 int，转失败返回 -1 */
    private int toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }
}
