package com.shuzhi.service;

import com.shuzhi.common.basemapper.BaseService;
import com.shuzhi.entity.Lighpole;

import java.util.List;


/**
 * @author shuzhi
 * @date 2019-08-12 10:42:37
 */

public interface LighpoleService extends BaseService<Lighpole> {

    List<Lighpole> findAlls();
}