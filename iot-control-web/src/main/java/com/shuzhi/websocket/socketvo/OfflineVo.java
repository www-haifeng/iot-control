package com.shuzhi.websocket.socketvo;

import com.shuzhi.entity.DeviceLoop;
import com.shuzhi.entity.DeviceStation;
import com.shuzhi.entity.Station;
import com.shuzhi.lcd.entities.IotLcdStatusTwo;
import com.shuzhi.led.entities.TStatusDto;
import com.shuzhi.light.entities.TLoopStateDto;
import com.shuzhi.mapper.DeviceStationMapper;
import com.shuzhi.service.DeviceStationService;
import com.shuzhi.service.StationService;
import com.shuzhi.websocket.ApplicationContextUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zgk
 * @description
 * @date 2019-08-01 9:12
 */
@Data
public class OfflineVo {

    /**
     * 设备id
     */
    private Long id;

    /**
     * 1-顶棚照明；2-灯箱照明；3-logo照明；4-LED；5-LCD
     */
    private Integer type;

    /**
     * 站名称
     */
    private String stationname;

    /**
     * 设备名称
     */
    private String devicename;

    /**
     * 站名称
     */
    private String name;

    /**
     * 离线时间
     */
    private String offlinetime;

    /**
     * 状态：1-在线；0-离线
     */
    private Integer state;

    /**
     * 封装照明设备信息的构造方法
     *
     * @param deviceLoop   照明设备信息
     * @param loopStateDto
     */
    OfflineVo(DeviceLoop deviceLoop, TLoopStateDto loopStateDto) {

        DeviceStationService deviceStationService = ApplicationContextUtils.get(DeviceStationService.class);
        //查出 对应的公交站id和名称
        DeviceStation deviceStationSelect = new DeviceStation();
        deviceStationSelect.setTypecode(deviceLoop.getTypecode());
        deviceStationSelect.setDeviceDid(deviceLoop.getDeviceDid());
        DeviceStation deviceStation = deviceStationService.selectOne(deviceStationSelect);
        if (deviceStation != null) {
            StationService stationService = ApplicationContextUtils.get(StationService.class);
            Station station = stationService.selectByPrimaryKey(deviceStation.getStationid());
            if (station != null) {
                this.name = station.getStationName();
                this.id = Long.valueOf(deviceLoop.getDeviceDid());
                if (loopStateDto.getState() == 1) {
                    if (StringUtils.isBlank(loopStateDto.getTimestamp())) {
                        this.offlinetime = "未获取到离线时间";

                    } else {
                        String[] split = loopStateDto.getTimestamp().split("\\.");
                        this.offlinetime = split[0];
                    }
                    this.offlinetime = loopStateDto.getTimestamp();
                    this.state = 0;
                } else {
                    this.state = 1;
                }
            }
        }
    }


    /**
     * 封装个lcd设备的构造方法
     *
     * @param iotLcdStatusTwo lcd设备信息
     */
    OfflineVo(IotLcdStatusTwo iotLcdStatusTwo) {

        if ("0".equals(iotLcdStatusTwo.getStatus())) {
            //通过设备号 查出站名
            DeviceStationMapper deviceStationMapper = ApplicationContextUtils.get(DeviceStationMapper.class);
            DeviceStation deviceStationSelect = new DeviceStation(iotLcdStatusTwo.getId());
            deviceStationSelect.setTypecode("5");
            DeviceStation deviceStation = deviceStationMapper.selectOne(deviceStationSelect);
            if (deviceStation != null) {
                this.name = deviceStation.getStationName();
            }
            this.id = Long.valueOf(iotLcdStatusTwo.getId());
            this.state = Integer.valueOf(iotLcdStatusTwo.getStatus());
            if (state == 0) {
                if (StringUtils.isBlank(iotLcdStatusTwo.getTimestamp())) {
                    this.offlinetime = "未获取到离线时间";
                } else {
                    String[] split = iotLcdStatusTwo.getTimestamp().split("\\.");
                    this.offlinetime = split[0];
                }
            }
        }
    }

    /**
     * 封装led设备的构造方法
     *
     * @param tStatusDto led设备信息
     */
    OfflineVo(TStatusDto tStatusDto) {
        if (tStatusDto.getState() == 0) {
            //通过设备号 查出站名
            DeviceStationMapper deviceStationMapper = ApplicationContextUtils.get(DeviceStationMapper.class);
            DeviceStation deviceStationSelect = new DeviceStation(tStatusDto.getId());
            deviceStationSelect.setTypecode("4");
            DeviceStation deviceStation = deviceStationMapper.selectOne(deviceStationSelect);
            if (deviceStation != null) {
                this.name = deviceStation.getStationName();
                this.devicename = deviceStation.getDeviceName();
            }
            this.id = Long.valueOf(tStatusDto.getId());
            this.state = tStatusDto.getState();
            if (state == 0) {
                if (StringUtils.isBlank(tStatusDto.getTimestamp())) {
                    this.offlinetime = "未获取到离线时间";
                } else {
                    String[] split = tStatusDto.getTimestamp().split("\\.");
                    this.offlinetime = split[0];
                }
            }
        }
    }

    /**
     * 封装所有设备的构造方法
     *
     * @param devices    所有设备信息
     * @param deviceLoop
     */
    OfflineVo(Devices devices, DeviceLoop deviceLoop) {
        this.id = devices.getId();
        this.type = Integer.valueOf(deviceLoop.getTypecode());
        this.offlinetime = devices.getTimestamp();
        this.devicename = deviceLoop.getDeviceName();
        this.state = devices.getState();
    }
}
