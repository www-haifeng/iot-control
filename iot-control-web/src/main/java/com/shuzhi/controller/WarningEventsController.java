package com.shuzhi.controller;

import com.shuzhi.entity.DeviceType;
import com.shuzhi.entity.WarningEvents;
import com.shuzhi.entity.WarningEventsVo;
import com.shuzhi.service.WarningEventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author huliang
 * @date 2019/8/18 11:20
 */
@RestController
@RequestMapping(value = "/warningEvents")
public class WarningEventsController {

    @Autowired
    private WarningEventsService warningEventsService;

    /**
     * 查询监测预警和事件管理
     */
    @RequestMapping(value = "/warningEventsAll", method = RequestMethod.GET)
    public List<WarningEvents> findWarningEvents(@RequestBody WarningEventsVo warningEventsVo) {
        return warningEventsService.findWarningEvents(warningEventsVo);
    }

    /**
     * 查询设备类型
     */
    @RequestMapping(value = "/deviceType", method = RequestMethod.GET)
    public List<DeviceType> findDeviceType() {
        return warningEventsService.findDeviceType();
    }

}
