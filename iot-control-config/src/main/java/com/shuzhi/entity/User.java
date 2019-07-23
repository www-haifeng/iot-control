package com.shuzhi.entity;

import com.shuzhi.common.basemapper.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */

@Table(name = "t_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    private Integer userId;

    /**
     * 用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 电话号,可以作为登录名
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 邮箱,可以当做登录名
     */
    @Column(name = "email")
    private String email;

    /**
     * 创建时间
     */
    @Column(name = "creation_time")
    private Date creationTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    private Date lastLoginTime;

    /**
     * 备注
     */
    @Column(name = "note")
    private String note;

    /**
     * 用户账户是否是过期
     */
    @Column(name = "is_account_non_expired")
    private Boolean isAccountNonExpired;

    /**
     * 用户是否已被锁定
     */
    @Column(name = "is_account_non_locked")
    private Boolean isAccountNonLocked;

    /**
     * 用户密码是否是过期
     */
    @Column(name = "is_credentials_non_expired")
    private Boolean isCredentialsNonExpired;

    /**
     * 用户是否启用
     */
    @Column(name = "is_enabled")
    private Boolean isEnabled;

    /**
     * 密码采用BCrypt
     */
    @Column(name = "password")
    private String password;

    /**
     * 登录名
     */
    @Column(name = "login_name")
    private String loginName;

    /**
     * 是否是管理员
     */
    @Column(name = "is_admin")
    private String isAdmin;

}
