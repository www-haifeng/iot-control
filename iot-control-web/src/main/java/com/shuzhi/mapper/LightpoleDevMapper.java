package com.shuzhi.mapper;

import com.shuzhi.entity.ControllerLights;
import com.shuzhi.entity.GroupLightpoleDev;
import com.shuzhi.entity.LightpoleDev;
import com.shuzhi.entity.LightpoleDevs;
import org.springframework.stereotype.Repository;
import com.shuzhi.common.basemapper.MyBaseMapper;

import java.util.List;


/**
 * @author shuzhi
 * @date 2019-08-12 10:42:37
 */

@Repository
public interface LightpoleDevMapper extends MyBaseMapper<LightpoleDev> {

    List<GroupLightpoleDev> groupLightpoleDev();
    public List<LightpoleDevs> lightpoleDev() ;
}
