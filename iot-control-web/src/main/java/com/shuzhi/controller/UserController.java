package com.shuzhi.controller;

import com.shuzhi.common.utils.Wrapper;
import com.shuzhi.entity.User;
import com.shuzhi.service.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户登录注册等
 *
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/test")
    public String registered() {
        return "OK";
    }

    /**
     * 通过id删除用户 做逻辑删除将该用户锁定
     *
     * @param userId 用户id
     * @return 删除结果
     */
    @RequestMapping("/removeUser/{userId}")
    public Wrapper removeUser2(@PathVariable Integer userId) {
        return userService.removeUser(userId);
    }

    /**
     * 修改用户信息
     *
     * @param user 要修改的用户信息
     * @return 修改结果
     */
    @RequestMapping("/updateUser")
    public Wrapper updateUser(User user){
        return userService.updateUser(user);
    }
}