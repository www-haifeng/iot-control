package com.shuzhi.websocket.socketvo;

import com.shuzhi.entity.DeviceLoop;
import com.shuzhi.mapper.DeviceLoopMapper;
import com.shuzhi.websocket.ApplicationContextUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zgk
 * @description 所有站的单站离线设备信息
 * @date 2019-08-01 15:00
 */
@Data
public class StationsVo {

    /**
     * 公交站id
     */
    private Integer stationid;

    /**
     * 公交站名称
     */
    private String stationname;

    /**
     * 离线设备数组
     */
    private List<OfflineVo> offlines = new ArrayList<>();


    StationsVo(DevicesMsg devicesMsg) {

        this.stationid = devicesMsg.getStationid();
        this.stationname = devicesMsg.getStationname();
        devicesMsg.getDevices().forEach(devices -> {
            if (devices.getState() == 0){
                //通过设备id获取设备信息
                DeviceLoopMapper deviceLoopMapper = ApplicationContextUtils.get(DeviceLoopMapper.class);
                DeviceLoop deviceLoopSelect = new DeviceLoop();
                deviceLoopSelect.setDeviceDid(String.valueOf(devices.getId()));
                deviceLoopSelect.setTypecode(String.valueOf(devices.getType()));
                DeviceLoop deviceLoop = deviceLoopMapper.selectOne(deviceLoopSelect);
                if (deviceLoop != null) {
                    OfflineVo offlineVo = new OfflineVo(devices, deviceLoop);
                    offlineVo.setStationname(stationname);
                    offlines.add(offlineVo);
                }
            }
        });
    }
}
