package com.shuzhi.controller;

import com.shuzhi.common.utils.Wrapper;
import com.shuzhi.entity.Role;
import com.shuzhi.service.RoleService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */

@RestController
@RequestMapping(value = "/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 保存角色
     *
     * @param role 角色信息
     * @return 保存结果
     */
    @RequestMapping("/saveRole")
    public Wrapper saveRole(Role role) {
        return roleService.saveRole(role);
    }


    /**
     * 批量删除角色 如果角色关联了用户或者目录则无法删除
     *
     * @param roleId 要删除的角色id
     * @return 删除结果
     */
    @RequestMapping("/removerRoleById/{roleId}")
    public Wrapper removerRoleById(@PathVariable Integer roleId) {
        return roleService.removerRoleById(roleId);
    }


    /**
     * 查询角色信息 如果不传id则查询所有的角色信息
     *
     * @param role id和分页信息
     * @return 查询结果
     */
    @RequestMapping("/findRole")
    public Wrapper findRole(Role role) {
        return roleService.findRole(role);
    }


    /**
     * 更新角色信息
     *
     * @param role 要更新的角色信息
     * @return 更新结果
     */
    @RequestMapping("/updateRole")
    public Wrapper updateRole(Role role) {
        return roleService.updateRole(role);
    }
}