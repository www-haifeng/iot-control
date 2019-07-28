package com.shuzhi.service;

import com.shuzhi.common.basemapper.BaseService;
import com.shuzhi.common.utils.Wrapper;
import com.shuzhi.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */

public interface UserService extends BaseService<User>, UserDetailsService {

    /**
     * 用户注册
     *
     * @param user 用户注册信息
     * @return 注册结果
     */
    Wrapper registered(User user);

    /**
     * 通过id删除用户 做逻辑删除将该用户锁定
     *
     * @param userId 用户id
     * @return 删除结果
     */
    Wrapper removeUser(Integer userId);

    /**
     * 修改用户信息
     *
     * @param user 要修改的用户信息
     * @return 修改结果
     */
    Wrapper updateUser(User user);
}