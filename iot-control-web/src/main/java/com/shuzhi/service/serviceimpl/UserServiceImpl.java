package com.shuzhi.service.serviceimpl;

import com.shuzhi.common.basemapper.BaseServiceImpl;
import com.shuzhi.common.utils.WrapMapper;
import com.shuzhi.common.utils.Wrapper;
import com.shuzhi.entity.Role;
import com.shuzhi.entity.User;
import com.shuzhi.entity.UserRole;
import com.shuzhi.function.Validation;
import com.shuzhi.mapper.RoleMapper;
import com.shuzhi.mapper.UserMapper;
import com.shuzhi.mapper.UserRoleMapper;
import com.shuzhi.service.UserService;
import com.shuzhi.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static com.shuzhi.eum.WebEum.*;


/**
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    private final UserMapper userMapper;

    private final UserRoleMapper userRoleMapper;

    private final RoleMapper roleMapper;

    public UserServiceImpl(UserMapper userMapper, UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    /**
     * 登录逻辑 返回用户信息即可 由SpringSecurity校验
     *
     * @param loginName 登录名
     * @return 用户信息
     */
    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        if (StringUtils.isNotBlank(loginName)) {
            return userMapper.selectByLoginName(loginName);
        }
        throw new IllegalStateException("请先登录");
    }

    /**
     * @param user 用户信息
     * @description 用户注册 用户密码使用BCrypt加密{@link org.springframework.security.crypto.bcrypt}
     * @author zgk
     * @date 2019-07-04 15:27
     */
    @Override
    public Wrapper registered(User user) {
        //验证参数
        return Optional.ofNullable(validation().check(user)).orElseGet(() -> {
            //初始化并保存用户信息
            userMapper.insertSelective(initialize(user));
            //设置一个默认权限 ROLE_USER
            setRule(user);
            return WrapMapper.ok();
        });
}

    /**
     * 通过id删除用户 做逻辑删除将该用户锁定
     *
     * @param userId 用户id
     * @return 删除结果
     */
    @Override
    public Wrapper removeUser(Integer userId) {
        User user = new User();
        user.setUserId(userId);
        user.setIsEnabled(false);
        return WrapMapper.handleResult(userMapper.updateByPrimaryKeySelective(user));
    }

    /**
     * 修改用户信息
     *
     * @param user 要修改的用户信息
     * @return 修改结果
     */
    @Override
    public Wrapper updateUser(User user) {
        //验证并更新
        return Optional.ofNullable(validation().check(user)).orElseGet(() -> WrapMapper.handleResult(userMapper.updateByPrimaryKeySelective(user)));
    }

    private void setRule(User user) {
        //当前系统时间
        Date date = new Date();
        UserRole userRole = new UserRole();
        //查询数据库中有没有ROLE_USER的角色 没有就加入一条
        Role roleSelect = new Role();
        roleSelect.setRoleCode("ROLE_USER");
        Role role = roleMapper.selectOne(roleSelect);
        if (role == null) {
            roleSelect.setCreationTime(date);
            roleSelect.setRoleCode("ROLE_USER");
            roleSelect.setRoleName("默认角色");
            roleMapper.insertSelective(roleSelect);
            userRole.setRoleId(roleMapper.selectOne(roleSelect).getRoleId());
        } else {
            userRole.setRoleId(role.getRoleId());
        }
        //保存到中间表
        User user1 = userMapper.selectOne(user);
        userRole.setUserId(user1.getUserId());
        userRole.setCreationTime(date);
        userRoleMapper.insertSelective(userRole);
    }


    private User initialize(User user) {
        //加密密码
        user.setPassword(SecurityUtils.encoder(user.getPassword()));
        //将初始化状态为true(可用)
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsEnabled(true);
        user.setCreationTime(new Date());

        return user;
    }

    /**
     * @description 验证参数
     * @author zgk
     * @date 2019-07-04 16:13
     */
    private Validation<User> validation() {
        User userSelect = new User();
        return o -> {
            //如果是更新 先查询要更新的是否存在
            if (o.getUserId() != null) {
                if (userMapper.selectByPrimaryKey(o.getUserId()) == null) {
                    return WrapMapper.wrap(REGISTERED_ERROR_6.getCode(), REGISTERED_ERROR_6.getMsg());
                }
            }
            if (StringUtils.isBlank(o.getUserName())) {
                return WrapMapper.wrap(REGISTERED_ERROR_1.getCode(), REGISTERED_ERROR_1.getMsg());
            } else {
                //判断该用户名是否存在
                userSelect.setUserName(o.getUserName());
                if (userMapper.selectCount(userSelect) != 0) {
                    return WrapMapper.wrap(REGISTERED_ERROR_3.getCode(), REGISTERED_ERROR_3.getMsg());
                }
            }
            if (StringUtils.isBlank(o.getLoginName())) {
                return WrapMapper.wrap(REGISTERED_ERROR_5.getCode(), REGISTERED_ERROR_5.getMsg());
            } else {
                //判断该登录名是否存在
                userSelect.setUserName(null);
                userSelect.setLoginName(o.getLoginName());
                if (userMapper.selectCount(userSelect) != 0) {
                    return WrapMapper.wrap(REGISTERED_ERROR_5.getCode(), REGISTERED_ERROR_5.getMsg());
                }
                //验证密码是否输入
                if (StringUtils.isBlank(o.getPassword())) {
                    return WrapMapper.wrap(REGISTERED_ERROR_2.getCode(), REGISTERED_ERROR_2.getMsg());
                }
            }
            return null;
        };
    }
}