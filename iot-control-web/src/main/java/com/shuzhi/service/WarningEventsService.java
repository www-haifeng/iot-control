package com.shuzhi.service;


import com.shuzhi.entity.DeviceType;
import com.shuzhi.entity.WarningEvents;
import com.shuzhi.entity.WarningEventsVo;

import java.util.List;


/**
 * @author huliang
 * @date 2019/8/18 11:22
 */
public interface WarningEventsService {
    List<WarningEvents> findWarningEvents(WarningEventsVo warningEventsVo);

    List<DeviceType> findDeviceType();
}
