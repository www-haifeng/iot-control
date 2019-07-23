package com.shuzhi.eum;

/**
 * @author zgk
 * @description
 * @date 2019-07-04 15:31
 */
public enum WebEum {

    /**
     * 注册失败
     */
    REGISTERED_ERROR_1(10001, "注册失败,用户名不能为空"),

    REGISTERED_ERROR_2(10002, "注册失败,密码不能为空"),

    REGISTERED_ERROR_3(10003, "注册失败,该用户名已存在"),

    REGISTERED_ERROR_4(10004, "注册失败,登录名不能为空"),

    REGISTERED_ERROR_5(10005, "注册失败,该登录名已存在"),

    REGISTERED_ERROR_6(10006, "更新失败,该用户不存在"),

    /**
     * 角色失败
     */
    ROLE_ERROR_1(20001, "保存失败,角色名不能个为空"),

    ROLE_ERROR_2(20002, "保存失败,角色名已存在"),

    ROLE_ERROR_3(20003, "保存失败,角色编号不能为空"),

    ROLE_ERROR_4(20004, "保存失败,角色编号已存在"),

    ROLE_ERROR_5(20005, "删除失败,该角色已被用户或目录使用"),





    /**
     * 目录失败
     */
    MENU_ERROR_1(30001, "保存失败,url不能为空"),

    MENU_ERROR_2(30002, "保存失败,该url已配置"),

    MENU_ERROR_3(30003, "保存失败,目录名称不能为空"),

    MENU_ERROR_4(30004, "保存失败,该目录名称已存在"),

    MENU_ERROR_5(30005, "目录列表为空"),

    MENU_ERROR_6(30006, "更新失败,要更新的目录不存在"),

    ;

    /**
     * The Type.
     */
    int code;
    /**
     * The Name.
     */
    String msg;

    WebEum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getMsg() {
        return msg;
    }

}
