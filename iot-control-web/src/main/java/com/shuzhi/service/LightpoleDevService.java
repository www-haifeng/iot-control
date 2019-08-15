package com.shuzhi.service;


import com.shuzhi.common.basemapper.BaseService;
import com.shuzhi.entity.ControllerLights;
import com.shuzhi.entity.GroupLightpoleDev;
import com.shuzhi.entity.LightpoleDev;
import com.shuzhi.entity.LightpoleDevs;

import java.util.List;

/**
 * @author shuzhi
 * @date 2019-08-12 10:42:37
 */

public interface LightpoleDevService extends BaseService<LightpoleDev> {

    public List<GroupLightpoleDev> groupLightpoleDev() ;

    public List<LightpoleDevs> lightpoleDev() ;

}