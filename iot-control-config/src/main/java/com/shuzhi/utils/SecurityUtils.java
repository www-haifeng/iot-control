package com.shuzhi.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author zgk
 * @description 安全框架相关工具类
 * @date 2019-07-05 13:20
 */
public class SecurityUtils {

    /**
     * 获取当前登录人的信息
     *
     * @return 当前登录人的信息
     */
    public static UserDetails getUserDetails() {
        return (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    /**
     * 加密密码
     *
     * @param password 密码
     * @return 加密后的密码
     */
    public static String encoder(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

}
