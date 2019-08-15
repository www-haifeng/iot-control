package com.shuzhi.config;

import com.shuzhi.mapper.RoleMenuMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.web.cors.CorsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zgk
 * @description security配置类
 * @date 2019-07-04 11:06
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final RoleMenuMapper roleMenuMapper;

    private final MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    private final MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    public WebSecurityConfig(RoleMenuMapper roleMenuMapper, MyAuthenticationSuccessHandler myAuthenticationSuccessHandler, MyAuthenticationFailureHandler myAuthenticationFailureHandler) {
        this.roleMenuMapper = roleMenuMapper;
        this.myAuthenticationSuccessHandler = myAuthenticationSuccessHandler;
        this.myAuthenticationFailureHandler = myAuthenticationFailureHandler;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry
                = http.authorizeRequests();
        //让Spring security放行所有preflight request
        registry.requestMatchers(CorsUtils::isPreFlightRequest).permitAll();
        //表单登录
        http
                //添加过滤器
                .addFilterAfter(dynamicallyUrlInterceptor(), FilterSecurityInterceptor.class)
                .authorizeRequests()
                //注册可以请求不拦截
               // .antMatchers("/websocket/**","/registered","/index","/websocket","/websocket/onClose/**").permitAll().anyRequest().authenticated()
                .antMatchers("/**").permitAll().anyRequest().authenticated()
                .and()
                .formLogin()
                //登录路径
                .loginProcessingUrl("/user/login")
                //添加验证成功处理器
                .successHandler(myAuthenticationSuccessHandler)
                //添加登录失败处理器
                .failureHandler(myAuthenticationFailureHandler)
                //登录成功跳转到哪里 不能和登录成功处理器一起配置
                //  .successForwardUrl("/user/test")
                //登录失败要跳转到哪里
               // .loginPage("/login/123")
                //失败页面
               // .failureUrl("/login/error")
                .and()
                //记住我
                .rememberMe()
                .and()
                //退出登录
                .logout()
                .and()
                //关闭csrf防火墙 否则无法post
                .csrf()
                .disable();
                //设置每个用户最大session为1  防止异地登录
                //.sessionManagement()
              //  .maximumSessions(1);
    }

    @Bean
    public DynamicallyUrlInterceptor dynamicallyUrlInterceptor() {
        DynamicallyUrlInterceptor interceptor = new DynamicallyUrlInterceptor();
        interceptor.setSecurityMetadataSource(new MyFilterSecurityMetadataSource(roleMenuMapper));

        //配置RoleVoter决策
        List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();
        decisionVoters.add(new RoleVoter());
        //设置认证决策管理器
        interceptor.setAccessDecisionManager(new DynamicallyUrlAccessDecisionManager(decisionVoters));
        return interceptor;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
