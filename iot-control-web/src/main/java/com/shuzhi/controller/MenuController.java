package com.shuzhi.controller;

import com.shuzhi.common.utils.Wrapper;
import com.shuzhi.entity.Menu;
import com.shuzhi.service.MenuService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author shuzhi
 *
 * @date 2019-07-04 15:04:42
 */

@RestController
@RequestMapping(value = "/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * 保存目录
     *
     * @param menu 要保存的目录
     * @return 保存结果
     */
    @RequestMapping("/saveMenu")
    public Wrapper saveMenu(Menu menu) {
        return menuService.saveMenu(menu);
    }

    /**
     * 查出所有的目录的树状结构
     *
     * @return 查询结果
     */
    @RequestMapping("/findAll")
    public Wrapper findAll(){
        return menuService.findAll();
    }

    /**
     * 通过id查出目录的详细信息
     *
     * @return 查询结果
     */
    @RequestMapping("/findById/{menuId}")
    public Wrapper findById(@PathVariable Integer menuId){
        return menuService.findById(menuId);
    }

    /**
     * 批量删除目录
     *
     * @param urlIds 要删除的目录
     * @return 删除了多少条
     */
    @RequestMapping("/removeMenu")
    public Wrapper removeMenu(Integer[] urlIds){
        return menuService.removeMenu(urlIds);
    }

    /**
     * 更新目录
     * @param menu 要更新的目录
     * @return 更新结果
     */
    @RequestMapping("/updateMenu")
    public Wrapper updateMenu(Menu menu){
        return menuService.updateMenu(menu);
    }
}