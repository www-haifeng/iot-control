package com.shuzhi.service.serviceimpl;

import com.shuzhi.common.basemapper.BaseServiceImpl;
import com.shuzhi.entity.ControllerLights;
import com.shuzhi.entity.GroupLightpoleDev;
import com.shuzhi.entity.LightpoleDev;
import com.shuzhi.entity.LightpoleDevs;
import com.shuzhi.mapper.LightpoleDevMapper;
import com.shuzhi.service.LightpoleDevService;
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
public class LightpoleDevServiceImpl extends BaseServiceImpl<LightpoleDev> implements LightpoleDevService {

    @Autowired
    private LightpoleDevMapper lightpoleDevMapper;
    @Override
    public List<GroupLightpoleDev> groupLightpoleDev() {
        return lightpoleDevMapper.groupLightpoleDev();
    }

    @Override
    public List<LightpoleDevs> lightpoleDev() {
        return lightpoleDevMapper.lightpoleDev();
    }

}