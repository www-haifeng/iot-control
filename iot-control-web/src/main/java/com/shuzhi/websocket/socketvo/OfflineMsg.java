package com.shuzhi.websocket.socketvo;

import com.shuzhi.lcd.entities.IotLcdStatusTwo;
import com.shuzhi.led.entities.TStatusDto;
import com.shuzhi.light.entities.TLoopStateDto;
import com.shuzhi.lightiotcomm.entities.ControllerApi;
import com.shuzhi.lightiotcomm.entities.TControllerState;
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

    //智联照明离线设备
    public void offlineLightMsg(List<ControllerApi> controllerStatus) {
        controllerStatus.forEach(tControllerState -> {

            if(0 == tControllerState.getOnline()){
                OfflineVo offlineVo = new OfflineVo();
                offlineVo.setId(tControllerState.getId());

                //N表示离线 Y 表示在线
                offlineVo.setState(0);
            /*if (tControllerState.getComm().equalsIgnoreCase("N")){
                offlineVo.setState(0);
            }
            if(tControllerState.getComm().equalsIgnoreCase("Y")) {
                offlineVo.setState(1);
            }*/
                offlineVo.setName(tControllerState.getName());
                offlineVo.setOfflinetime(String.valueOf(tControllerState.getOnoffTime()));
                offlines.add(offlineVo);
            }



        });



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
                offlineVo.setId(Long.valueOf(tStatusDto.getId()));
                offlineVo.setName(tStatusDto.getName());
                offlineVo.setOfflinetime(tStatusDto.getTimestamp());
                offlineVo.setState(Integer.valueOf(tStatusDto.getState()));
                offlines.add(offlineVo);
            };
        });

    }
}
