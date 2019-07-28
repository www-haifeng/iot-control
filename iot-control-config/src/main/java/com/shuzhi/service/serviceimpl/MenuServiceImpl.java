package com.shuzhi.service.serviceimpl;

import com.shuzhi.common.basemapper.BaseServiceImpl;
import com.shuzhi.common.utils.WrapMapper;
import com.shuzhi.common.utils.Wrapper;
import com.shuzhi.entity.Menu;
import com.shuzhi.entity.RoleMenu;
import com.shuzhi.function.Validation;
import com.shuzhi.mapper.MenuMapper;
import com.shuzhi.mapper.RoleMenuMapper;
import com.shuzhi.service.MenuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.shuzhi.eum.WebEum.*;


/**
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MenuServiceImpl extends BaseServiceImpl<Menu> implements MenuService {

    private final MenuMapper menuMapper;

    private final RoleMenuMapper roleMenuMapper;

    public MenuServiceImpl(MenuMapper menuMapper, RoleMenuMapper roleMenuMapper) {
        this.menuMapper = menuMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    /**
     * 保存目录信息
     *
     * @param menu 要保存的目录
     * @return 保存结果
     */
    @Override
    public Wrapper saveMenu(Menu menu) {
        //验证参数
       return Optional.ofNullable(validation().check(menu)).orElseGet(() -> {
           //如果是一级目录 把父id设置为0
           menu.setParentMenu(Optional.ofNullable(menu.getParentMenu()).orElse(0));
           return WrapMapper.handleResult(menuMapper.insertSelective(menu));
        });
    }

    /**
     * 查询所有目录的树状结构 递归遍历
     *
     * @return 查询结果
     */
    @Override
    public Wrapper findAll() {
        //查询出所有的一级目录
        List<Menu> parentMenuList = menuMapper.selectParentMenu(0);
        if (parentMenuList.isEmpty()) {
            WrapMapper.wrap(MENU_ERROR_5.getCode(), MENU_ERROR_5.getMsg());
        }
        //递归遍历
        return WrapMapper.ok(recursive(parentMenuList));
    }

    /**
     * 批量删除目录递归删除子目录
     *
     * @param urlIds 要删除的目录
     * @return 删除了多少条
     */
    @Override
    public Wrapper removeMenu(Integer[] urlIds) {
        //记录删除了多少条
        AtomicInteger delete = new AtomicInteger();
        //遍历查询
        for (Integer urlId : urlIds) {
            Optional.ofNullable(menuMapper.selectByPrimaryKey(urlId)).ifPresent(menu -> {
                delete.getAndIncrement();
                //递归删除
                recursiveRemove(menu);
            });
        }
        return WrapMapper.handleResult(delete);
    }

    /**
     * 更新目录
     *
     * @param menu 要更新的目录
     * @return 更新结果
     */
    @Override
    public Wrapper updateMenu(Menu menu) {
        //验证参数
       return Optional.ofNullable(validation().check(menu)).orElseGet(() -> WrapMapper.handleResult(menuMapper.updateByPrimaryKey(menu)));
    }

    /**
     * 通过id查出目录的详细信息
     *
     * @return 查询结果
     * @param menuId 目录ID
     */
    @Override
    public Wrapper findById(Integer menuId) {
        return WrapMapper.ok(menuMapper.selectByPrimaryKey(menuId));
    }

    private void recursiveRemove(Menu menu) {
        //删除父目录
        menuMapper.deleteByPrimaryKey(menu.getUrlId());
        //删除中间表中的关联
        RoleMenu roleMenuSelect = new RoleMenu();
        roleMenuSelect.setUrlId(menu.getUrlId());
        roleMenuMapper.delete(roleMenuSelect);

        //查询是否存在子目录
        Menu menuSelect = new Menu();
        menuSelect.setParentMenu(menu.getUrlId());
        List<Menu> select = menuMapper.select(menuSelect);
        //如果有则删除子目录
        if (!select.isEmpty()) {
            select.forEach(this::recursiveRemove);
        }
    }

    /**
     * 递归查询所有的目录
     *
     * @param parentMenuList 所有的一级目录
     * @return 查询结果
     */
    private List<Menu> recursive(List<Menu> parentMenuList) {

        Menu menuSelect = new Menu();
        //遍历父目录
        parentMenuList.forEach(menu -> {
            //查询是否有子菜单
            menuSelect.setParentMenu(menu.getUrlId());
            List<Menu> select = menuMapper.select(menuSelect);
            if (!select.isEmpty()) {
                menu.setMenuList(select);
                //递归
                recursive(select);
            }
        });
        return parentMenuList;
    }

    private Validation<Menu> validation() {
        Menu menuSelect = new Menu();
        return menu -> {
            //验证url
            if (StringUtils.isBlank(menu.getUrl())) {
                WrapMapper.wrap(MENU_ERROR_1.getCode(), MENU_ERROR_1.getMsg());
            }
            menuSelect.setUrl(menu.getUrl());
            if (menuMapper.select(menuSelect) != null) {
                WrapMapper.wrap(MENU_ERROR_2.getCode(), MENU_ERROR_2.getMsg());
            }
            //验证名称
            if (StringUtils.isBlank(menu.getUrlName())) {
                WrapMapper.wrap(MENU_ERROR_3.getCode(), MENU_ERROR_3.getMsg());
            }
            menuSelect.setUrl(null);
            menuSelect.setUrlName(menu.getUrlName());
            if (menuMapper.select(menuSelect) != null) {
                WrapMapper.wrap(MENU_ERROR_4.getCode(), MENU_ERROR_4.getMsg());
            }

            //验证要更新的目录是否还存在
            if (menu.getUrlId() != null) {
                if (menuMapper.selectByPrimaryKey(menu.getUrlId()) == null) {
                    WrapMapper.wrap(MENU_ERROR_6.getCode(), MENU_ERROR_6.getMsg());
                }
            }
            return null;
        };
    }
}