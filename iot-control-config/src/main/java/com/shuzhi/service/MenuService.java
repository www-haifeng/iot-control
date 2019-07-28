package com.shuzhi.service;

import com.shuzhi.common.basemapper.BaseService;
import com.shuzhi.common.utils.Wrapper;
import com.shuzhi.entity.Menu;


/**
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */

public interface MenuService extends BaseService<Menu> {

    /**
     * 保存目录信息
     *
     * @param menu 要保存的目录
     * @return 保存结果
     */
    Wrapper saveMenu(Menu menu);

    /**
     * 查询出所有的目录的树状结构
     *
     * @return 查询结果
     */
    Wrapper findAll();

    /**
     * 批量删除目录
     *
     * @param urlIds 要删除的目录
     * @return 删除了多少条
     */
    Wrapper removeMenu(Integer[] urlIds);

    /**
     * 更新目录
     *
     * @param menu 要更新的目录
     * @return 更新结果
     */
    Wrapper updateMenu(Menu menu);

    /**
     * 通过id查出目录的详细信息
     *
     * @return 查询结果
     * @param menuId
     */
    Wrapper findById(Integer menuId);
}