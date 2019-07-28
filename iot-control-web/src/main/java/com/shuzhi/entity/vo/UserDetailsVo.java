package com.shuzhi.entity.vo;

import com.shuzhi.common.basemapper.BaseEntity;
import com.shuzhi.entity.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * 用来封装用户登录信息
 *
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */

@Table(name = "t_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDetailsVo extends BaseEntity implements Serializable, UserDetails {
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
    private Boolean isAdmin;

    /**
     * 权限信息
     */
    private List<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //遍历封装
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        roles.forEach(s -> grantedAuthorities.add(new SimpleGrantedAuthority(s.getRoleCode())));
        return grantedAuthorities;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
