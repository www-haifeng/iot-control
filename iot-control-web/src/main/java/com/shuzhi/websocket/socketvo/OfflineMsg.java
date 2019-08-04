package com.shuzhi.websocket.socketvo;

import com.shuzhi.entity.DeviceLoop;
import com.shuzhi.lcd.entities.IotLcdStatusTwo;
import com.shuzhi.led.entities.TStatusDto;
import com.shuzhi.light.entities.TLoopStateDto;
import com.shuzhi.service.DeviceLoopService;
import com.shuzhi.websocket.ApplicationContextUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zgk
 * @description
 * @date 2019-08-01 9:11
 */
@Data
public class OfflineMsg {

    List<OfflineVo> offlines = new ArrayList<>();

    public void offlineLightMsg(List<TLoopStateDto> loopStatus) {

        loopStatus.forEach(loopStateDto -> {

            if (loopStateDto.getState() == 1) {
                //通过回路号查询这个是什么设备
                DeviceLoopService deviceLoopService = ApplicationContextUtils.get(DeviceLoopService.class);
                DeviceLoop deviceLoopSelect = new DeviceLoop();
                deviceLoopSelect.setLoop(loopStateDto.getLoop());
                deviceLoopSelect.setGatewayDid(loopStateDto.getGatewayId());
                DeviceLoop deviceLoop = deviceLoopService.selectOne(deviceLoopSelect);
                if (deviceLoop != null) {
                    if(StringUtils.equals(deviceLoop.getTypecode(), "1") || StringUtils.equals(deviceLoop.getTypecode(), "2") || StringUtils.equals(deviceLoop.getTypecode(), "3")){
                        OfflineVo offlineVo = new OfflineVo(deviceLoop, loopStateDto);
                        offlines.add(offlineVo);
                    }
                }
            }
        });

    }

    public void offlineLcdMsg(List<IotLcdStatusTwo> allStatusByRedis) {

        allStatusByRedis.forEach(iotLcdStatusTwo -> {
            OfflineVo offlineVo = new OfflineVo(iotLcdStatusTwo);
            offlines.add(offlineVo);
        });


    }

    public void offlineLedMsg(List<TStatusDto> allStatus) {

        allStatus.forEach(tStatusDto -> {
            OfflineVo offlineVo = new OfflineVo(tStatusDto);
            offlines.add(offlineVo);
        });

    }
}
