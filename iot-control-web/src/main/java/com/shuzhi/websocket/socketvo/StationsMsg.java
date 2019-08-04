package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zgk
 * @description 所有站的单站离线设备信息的集合
 * @date 2019-08-01 15:21
 */
@Data
public class StationsMsg {

    List<StationsVo> stations = new ArrayList<>();

    public StationsMsg(List<DevicesMsg> lights) {

        lights.forEach(devicesMsg -> {

            StationsVo stationsVo = new StationsVo(devicesMsg);
            stations.add(stationsVo);

        });
    }
}
