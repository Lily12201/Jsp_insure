package com.seiryo.entity;

public class AdminUser {

    private Integer admin_id;
    private String admin_account;
    private String admin_password;
    private String admin_name;
    /** 1=総管理者 2=一般管理者 */
    private Integer admin_role;
    /** 1启用 0禁用 */
    private Integer admin_status;
    private String created_at;
    //无参构造
    public AdminUser() {
        super();
    }
    //有参构造
    public AdminUser(Integer admin_id, String admin_account, String admin_password,
                     String admin_name, Integer admin_status, String created_at) {
        super();
        this.admin_id = admin_id;
        this.admin_account = admin_account;
        this.admin_password = admin_password;
        this.admin_name = admin_name;
        this.admin_status = admin_status;
        this.created_at = created_at;
    }
    //get和set方法
    public Integer getAdmin_id() {
        return admin_id;
    }
    public void setAdmin_id(Integer admin_id) {
        this.admin_id = admin_id;
    }

    public String getAdmin_account() {
        return admin_account;
    }
    public void setAdmin_account(String admin_account) {
        this.admin_account = admin_account;
    }

    public String getAdmin_password() {
        return admin_password;
    }
    public void setAdmin_password(String admin_password) {
        this.admin_password = admin_password;
    }

    public String getAdmin_name() {
        return admin_name;
    }
    public void setAdmin_name(String admin_name) {
        this.admin_name = admin_name;
    }

    public Integer getAdmin_role() {
        return admin_role;
    }
    public void setAdmin_role(Integer admin_role) {
        this.admin_role = admin_role;
    }

    public Integer getAdmin_status() {
        return admin_status;
    }
    public void setAdmin_status(Integer admin_status) {
        this.admin_status = admin_status;
    }

    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "AdminUser [admin_id=" + admin_id + ", admin_account=" + admin_account
                + ", admin_password=" + admin_password + ", admin_name=" + admin_name
                + ", admin_role=" + admin_role + ", admin_status=" + admin_status
                + ", created_at=" + created_at + "]";
    }
}
