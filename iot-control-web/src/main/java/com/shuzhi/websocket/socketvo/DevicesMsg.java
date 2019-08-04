package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zgk
 * @description 封装设备消息
 * @date 2019-07-22 13:58
 */
@Data
public class DevicesMsg {

    /**
     * 公交站id
     */
    private Integer stationid;

    /**
     * 公交站名称
     */
    private String stationname;

    /**
     * 设备信息集合
     */
    private List<Devices> devices = new ArrayList<>();


    public void setData(DevicesMsg devicesMsg1) {

        devices.addAll(devicesMsg1.getDevices());

    }
}
