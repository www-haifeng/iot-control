package com.shuzhi.mapper;

import com.shuzhi.common.basemapper.MyBaseMapper;
import com.shuzhi.entity.Menu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */

@Repository
public interface MenuMapper extends MyBaseMapper<Menu> {

    /**
     * 查询出所有的一级目录
     *
     * @param i 一级目录标识
     * @return 查询结果
     */
    List<Menu> selectParentMenu(@Param("i") int i);
}
