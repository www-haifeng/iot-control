package com.shuzhi.service.serviceimpl;

import com.github.pagehelper.PageHelper;
import com.shuzhi.common.basemapper.BaseServiceImpl;
import com.shuzhi.common.utils.WrapMapper;
import com.shuzhi.common.utils.Wrapper;
import com.shuzhi.entity.Role;
import com.shuzhi.entity.RoleMenu;
import com.shuzhi.entity.UserRole;
import com.shuzhi.function.Validation;
import com.shuzhi.mapper.RoleMapper;
import com.shuzhi.mapper.RoleMenuMapper;
import com.shuzhi.mapper.UserRoleMapper;
import com.shuzhi.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.shuzhi.eum.WebEum.*;


/**
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService {

    private final RoleMapper roleMapper;

    private final UserRoleMapper userRoleMapper;

    private final RoleMenuMapper roleMenuMapper;

    public RoleServiceImpl(RoleMapper roleMapper, UserRoleMapper userRoleMapper, RoleMenuMapper roleMenuMapper) {
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    /**
     * 保存角色
     *
     * @param role 角色信息
     * @return 保存结果
     */
    @Override
    public Wrapper saveRole(Role role) {
        //验证参数并保存
        return Optional.ofNullable(validation().check(role)).orElseGet(() -> WrapMapper.handleResult(roleMapper.insertSelective(role)));
    }

    /**
     * 批量删除角色 如果角色关联了用户或者目录则无法删除
     *
     * @param roleId 要删除的角色id
     * @return 删除结果
     */
    @Override
    public Wrapper removerRoleById(Integer roleId) {
        //验证是否能被删除
        return Optional.ofNullable(validationDelete().check(roleId)).orElseGet(() -> WrapMapper.ok(roleMapper.deleteByPrimaryKey(roleId)));
    }

    /**
     * 查询角色信息 如果不传id则查询所有的角色信息
     *
     * @param role id和分页信息
     * @return 查询结果
     */
    @Override
    public Wrapper findRole(Role role) {

        //如果没有分页信息添加默认值
        role.setPageNum(Optional.ofNullable(role.getPageNum()).orElse(1));
        role.setPageSize(Optional.ofNullable(role.getPageSize()).orElse(10));
        //分页查询
        PageHelper.startPage(role.getPageNum(), role.getPageSize());
        List<Role> select = roleMapper.select(role);
        return WrapMapper.handleResult(select);
    }

    /**
     * 更新角色信息
     *
     * @param role 要更新的角色信息
     * @return 更新结果
     */
    @Override
    public Wrapper updateRole(Role role) {
        //判断并更新
        return Optional.ofNullable(validation().check(role)).orElseGet(() -> WrapMapper.handleResult(roleMapper.updateByPrimaryKey(role)));
    }

    private Validation<Integer> validationDelete() {
        UserRole userRoleSelect = new UserRole();
        RoleMenu roleMenuSelect = new RoleMenu();
        return integer -> {
            //查询角色用户中间表 如果存在则无法删除
            userRoleSelect.setRoleId(integer);
            roleMenuSelect.setRoleId(integer);
            if (!userRoleMapper.select(userRoleSelect).isEmpty() || !roleMenuMapper.select(roleMenuSelect).isEmpty()) {
                return WrapMapper.wrap(ROLE_ERROR_5.getCode(), ROLE_ERROR_5.getMsg());
            }
            return null;
        };
    }

    /**
     * 验证参数
     *
     * @return 验证结果
     */
    private Validation<Role> validation() {
        String roleWith = "ROLE_";
        Role roleSelect = new Role();
        return role -> {
            //判断角色名是否重复
            roleSelect.setRoleName(role.getRoleName());
            if (StringUtils.isBlank(role.getRoleName())) {
                return WrapMapper.wrap(ROLE_ERROR_1.getCode(), ROLE_ERROR_1.getMsg());
            } else {
                //判断是否是ROLE_开头 不是就加上
                if (!role.getRoleName().startsWith(roleWith)) {
                    role.setRoleName(roleWith + role.getRoleName());
                }
            }
            if (roleMapper.selectOne(roleSelect) != null) {
                return WrapMapper.wrap(ROLE_ERROR_2.getCode(), ROLE_ERROR_2.getMsg());
            }

            //判断角色编号是否重复
            if (StringUtils.isBlank(role.getRoleCode())) {
                return WrapMapper.wrap(ROLE_ERROR_3.getCode(), ROLE_ERROR_3.getMsg());
            }
            roleSelect.setRoleName(null);
            roleSelect.setRoleCode(role.getRoleCode());
            if (roleMapper.selectOne(roleSelect) != null) {
                return WrapMapper.wrap(ROLE_ERROR_4.getCode(), ROLE_ERROR_4.getMsg());
            }
            return null;
        };
    }
}