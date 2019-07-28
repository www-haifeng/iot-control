package com.shuzhi.config;

import com.shuzhi.entity.vo.RoleMenuVo;
import com.shuzhi.mapper.RoleMenuMapper;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author zgk
 * @description 自定义动态加载资源角色
 * @date 2019-07-04 11:45
 */
@Component
public class MyFilterSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private RoleMenuMapper roleMenuMapper;

    MyFilterSecurityMetadataSource(RoleMenuMapper roleMenuMapper) {
        this.roleMenuMapper = roleMenuMapper;
    }

    /**
     * 在我们初始化的权限数据中找到对应当前url的权限数据
     *
     * @param object 定义的过滤器
     * @return 权限
     * @throws IllegalArgumentException IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation fi = (FilterInvocation) object;
        HttpServletRequest request = fi.getRequest();

        // Lookup your database (or other source) using this information and populate the
        // list of attributes (这里初始话权限数据)
        //遍历我们初始化的权限数据，找到对应的url对应的权限
        Map<RequestMatcher, Collection<ConfigAttribute>> map = new HashMap<>(16);
        //从数据库中查询权限和资源信息
        List<RoleMenuVo> roleMenuVos = roleMenuMapper.selectUrlAndRole();
        //遍历资源
        roleMenuVos.stream().filter(roleMenuVo -> roleMenuVo.getUrl() != null)
                .forEach(roleMenuVo -> {
                    ArrayList<ConfigAttribute> configs = new ArrayList<>();
                    //遍历角色
                    roleMenuVo.getRoles().forEach(role -> {
                        SecurityConfig config = new SecurityConfig(role.getRoleCode());
                        configs.add(config);
                    });
                    AntPathRequestMatcher matcher = new AntPathRequestMatcher(roleMenuVo.getUrl());
                    map.put(matcher, configs);
                });
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : map
                .entrySet()) {
            if (entry.getKey().matches(request)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
