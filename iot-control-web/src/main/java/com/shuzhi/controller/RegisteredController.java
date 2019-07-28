package com.shuzhi.controller;

import com.shuzhi.common.utils.Wrapper;
import com.shuzhi.entity.User;
import com.shuzhi.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zgk
 * @description 注册
 * @date 2019-07-08 9:27
 */
@RestController
public class RegisteredController {


    private final UserService userService;

    public RegisteredController(UserService userService) {
        this.userService = userService;
    }

    /**
     * @param user 用户信息
     * @description 用户注册
     * @author zgk
     * @date 2019-07-04 15:25
     */
    @RequestMapping("/registered")
    public Wrapper registered(User user) {
        return userService.registered(user);
    }
}
