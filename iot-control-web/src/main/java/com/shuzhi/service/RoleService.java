package com.shuzhi.service;

import com.shuzhi.common.basemapper.BaseService;
import com.shuzhi.common.utils.Wrapper;
import com.shuzhi.entity.Role;


/**
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */

public interface RoleService extends BaseService<Role> {

    /**
     * 保存角色
     *
     * @param role 角色信息
     * @return 保存结果
     */
    Wrapper saveRole(Role role);

    /**
     * 批量删除角色 如果角色关联了用户或者目录则无法删除
     *
     * @param roleId 要删除的角色id
     * @return 删除结果
     */
    Wrapper removerRoleById(Integer roleId);

    /**
     * 查询角色信息 如果不传id则查询所有的角色信息
     *
     * @param role id和分页信息
     * @return 查询结果
     */
    Wrapper findRole(Role role);

    /**
     * 更新角色信息
     *
     * @param role 要更新的角色信息
     * @return 更新结果
     */
    Wrapper updateRole(Role role);
}