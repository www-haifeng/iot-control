package com.shuzhi.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuzhi.common.utils.WrapMapper;
import com.shuzhi.entity.User;
import com.shuzhi.entity.vo.UserDetailsVo;
import com.shuzhi.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zgk
 * @description 登录成功的认证处理器
 */
@Slf4j
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    private final UserMapper userMapper;

    public MyAuthenticationSuccessHandler(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        //登录成功后保存最后登录时间
        //保存最后登录时间
        Date date = new Date();
        UserDetailsVo userDetailsVo = (UserDetailsVo) authentication.getPrincipal();
        User user = new User();
        user.setLoginName(userDetailsVo.getLoginName());
        user.setLastLoginTime(date);
        userMapper.updateByLoginName(user);

        //返回状态码 200
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setContentType("application/json;charset=utf-8");
        SimpleResponse simpleResponse = new SimpleResponse(userDetailsVo.getIsAdmin());
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(WrapMapper.wrap(1,"登录成功",simpleResponse)));
        log.info("用户 :"+userDetailsVo.getUsername()+" 登录成功 时间 : {}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
    }
}
