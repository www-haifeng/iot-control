package com.shuzhi.mapper;

import com.shuzhi.common.basemapper.MyBaseMapper;
import com.shuzhi.entity.RoleMenu;
import com.shuzhi.entity.vo.RoleMenuVo;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */

@Repository
public interface RoleMenuMapper extends MyBaseMapper<RoleMenu> {

    /**
     * 封装资源和角色
     *
     * @return 资源和角色信息
     */
    List<RoleMenuVo> selectUrlAndRole();
}
