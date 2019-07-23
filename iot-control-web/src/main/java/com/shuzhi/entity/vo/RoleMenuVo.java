package com.shuzhi.entity.vo;

import com.shuzhi.entity.Role;
import lombok.Data;

import java.util.List;

/**
 * @author zgk
 * @description 封装资源权限信息
 * @date 2019-07-05 15:04
 */
@Data
public class RoleMenuVo {

    /**
     * 资源路径 这里可以使用通配符 比如 /user/role/**
     */
    private String url;

    /**
     * 角色集合
     */
    private List<Role> roles;

}
