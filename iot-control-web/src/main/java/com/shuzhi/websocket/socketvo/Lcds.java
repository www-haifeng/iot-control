package com.shuzhi.websocket.socketvo;

import com.shuzhi.entity.DeviceStation;
import com.shuzhi.lcd.entities.IotLcdStatusTwo;
import com.shuzhi.service.DeviceStationService;
import com.shuzhi.websocket.ApplicationContextUtils;
import lombok.Data;

import java.util.List;

/**
 * @author zgk
 * @description
 * @date 2019-07-18 17:04
 */
@Data
public class Lcds{

    private List<IotLcdStatusTwo> allStatusByRedis;

    public Lcds(List<IotLcdStatusTwo> allStatusByRedis) {
        //查询出该设备所属的公交站
        DeviceStationService deviceStationService = ApplicationContextUtils.get(DeviceStationService.class);
        DeviceStation deviceStationSelect = new DeviceStation();
        allStatusByRedis.forEach(iotLcdStatusTwo -> {
            deviceStationSelect.setDeviceDid(iotLcdStatusTwo.getId());
            deviceStationSelect.setTypecode("5");
            DeviceStation deviceStation = deviceStationService.selectOne(deviceStationSelect);
            if (deviceStation != null){
                iotLcdStatusTwo.setStationid(deviceStation.getStationid());
                iotLcdStatusTwo.setStationname(deviceStation.getStationName());
            }else {
                iotLcdStatusTwo.setStationid(0);
                iotLcdStatusTwo.setStationname("该设备没有配置站台");
            }
        });
        this.allStatusByRedis = allStatusByRedis;
    }
}
