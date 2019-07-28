package com.shuzhi.mapper;

import com.shuzhi.common.basemapper.MyBaseMapper;
import com.shuzhi.entity.User;
import com.shuzhi.entity.vo.UserDetailsVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


/**
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */

@Repository
public interface UserMapper extends MyBaseMapper<User> {

    /**
     * 登录查询
     *
     * @param loginName 登录名
     * @return 用户信息
     */
    UserDetailsVo selectByLoginName(@Param("loginName") String loginName);

    /**
     * 保存最后登录时间
     *
     * @param user 用户信息
     */
    void updateByLoginName(User user);
}
