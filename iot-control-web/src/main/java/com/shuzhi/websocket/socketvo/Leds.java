package com.shuzhi.websocket.socketvo;

import com.shuzhi.entity.DeviceStation;
import com.shuzhi.led.entities.TStatusDto;
import com.shuzhi.service.DeviceStationService;
import com.shuzhi.websocket.ApplicationContextUtils;
import lombok.Data;

import java.util.List;

/**
 * @author zgk
 * @description
 * @date 2019-07-15 15:52
 */
@Data
public class Leds {
    List<TStatusDto> leds;

    public Leds(List<TStatusDto> allStatus) {
        //查询出该设备所属的公交站
        DeviceStationService deviceStationService = ApplicationContextUtils.get(DeviceStationService.class);
        DeviceStation deviceStationSelect = new DeviceStation();
        allStatus.forEach(tStatusDto -> {
            deviceStationSelect.setDeviceDid(tStatusDto.getId());
            deviceStationSelect.setTypecode("4");
            DeviceStation deviceStation = deviceStationService.selectOne(deviceStationSelect);
            if (deviceStation != null){
                tStatusDto.setStationid(deviceStation.getStationid());
                tStatusDto.setStationname(deviceStation.getStationName());
            }else {
                tStatusDto.setStationid(0);
                tStatusDto.setStationname("该设备没有配置站台");
            }
        });

        this.leds = allStatus;
    }
}
