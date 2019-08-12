package com.shuzhi.service.serviceimpl;

import com.shuzhi.common.basemapper.BaseServiceImpl;
import com.shuzhi.entity.Lighpole;
import com.shuzhi.mapper.LighpoleMapper;
import com.shuzhi.service.LighpoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author shuzhi
 * @date 2019-08-12 10:42:37
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class LighpoleServiceImpl extends BaseServiceImpl<Lighpole> implements LighpoleService {

    @Autowired
    private LighpoleMapper lighpoleMapper;
    @Override
    public List<Lighpole> findAlls() {
        return lighpoleMapper.findAlls();
    }
}