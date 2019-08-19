package com.shuzhi.service.serviceimpl;

import com.shuzhi.entity.DeviceType;
import com.shuzhi.entity.WarningEvents;
import com.shuzhi.entity.WarningEventsVo;
import com.shuzhi.frt.service.TEventService;
import com.shuzhi.led.entities.WarningEventsLedVo;
import com.shuzhi.led.service.TEventLedService;
import com.shuzhi.lightiotcomm.service.LightIotCommServiceApi;
import com.shuzhi.mapper.WarningEventsMapper;
import com.shuzhi.service.WarningEventsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huliang
 * @date 2019/8/18 11:23
 */
@Service
public class WarningEventsServiceImpl implements WarningEventsService {
    private final WarningEventsMapper warningEventsMapper;

    private final LightIotCommServiceApi lightIotCommServiceApi;

    private final TEventLedService tEventLedService;

    private final TEventService tEventService;

    public WarningEventsServiceImpl(WarningEventsMapper warningEventsMapper, LightIotCommServiceApi lightIotCommServiceApi, TEventLedService tEventLedService, TEventService tEventService) {
        this.warningEventsMapper = warningEventsMapper;
        this.lightIotCommServiceApi = lightIotCommServiceApi;
        this.tEventLedService = tEventLedService;
        this.tEventService = tEventService;
    }


    /**
     * 201-离线报警；202-在线报警 事件类型 101-开；102-关；103-在线；104-离线;
     * @param warningEventsVo
     * @return
     */
    @Override
    public List<WarningEvents> findWarningEvents(WarningEventsVo warningEventsVo) {
        switch (warningEventsVo.getDeviceName()){
            //led
            case 2:
                WarningEventsLedVo warningEventsLedVo = new WarningEventsLedVo();
                BeanUtils.copyProperties(warningEventsVo,warningEventsLedVo);
                tEventLedService.findAll(warningEventsLedVo);
                break;
             //环测
            case 5:
                break;
            //集中控制器
            case 4:
                break;
                default:
        }
        return null;
    }

    @Override
    public List<DeviceType> findDeviceType() {
        return null;
    }
}
