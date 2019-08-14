package com.shuzhi.websocket.socketvo;

import com.shuzhi.lcd.entities.IotLcdStatusTwo;
import com.shuzhi.led.entities.TStatusDto;
import com.shuzhi.light.entities.TLoopStateDto;
import lombok.Data;

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

    }

    public void offlineLcdMsg(List<IotLcdStatusTwo> allStatusByRedis) {

        allStatusByRedis.forEach(iotLcdStatusTwo -> {
            if (iotLcdStatusTwo.getOnoff() == 0){
                OfflineVo offlineVo = new OfflineVo();//TODO 离线设备
                offlineVo.setId(Long.valueOf(iotLcdStatusTwo.getId()));
                offlineVo.setName(iotLcdStatusTwo.getName());
                offlineVo.setOfflinetime(iotLcdStatusTwo.getTimestamp());
                offlineVo.setState(Integer.valueOf(iotLcdStatusTwo.getStatus()));
                offlines.add(offlineVo);
            }
        });


    }

    public void offlineLedMsg(List<TStatusDto> allStatus) {

        allStatus.forEach(tStatusDto -> {
            if (tStatusDto.getOnoff() == 0){
                OfflineVo offlineVo = new OfflineVo();//TODO 离线设备
                offlineVo.setId(Long.valueOf(tStatusDto.getId()+""));
                offlineVo.setName(tStatusDto.getName());
                offlineVo.setOfflinetime(tStatusDto.getTimestamp());
                offlineVo.setState(Integer.valueOf(tStatusDto.getState()));
                offlines.add(offlineVo);
            };
        });

    }
}
