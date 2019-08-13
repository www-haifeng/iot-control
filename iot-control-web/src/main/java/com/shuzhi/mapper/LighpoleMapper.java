package com.shuzhi.mapper;

import com.shuzhi.common.basemapper.MyBaseMapper;
import com.shuzhi.entity.Lighpole;
import com.shuzhi.entity.Lighpoles;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author shuzhi
 * @date 2019-08-12 10:42:37
 */

@Repository
public interface LighpoleMapper extends MyBaseMapper<Lighpole> {

    List<Lighpoles> findAlls();
}
