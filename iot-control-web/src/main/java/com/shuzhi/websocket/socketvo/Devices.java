package com.shuzhi.websocket.socketvo;

import com.shuzhi.entity.DeviceLoop;
import com.shuzhi.lcd.entities.IotLcdStatusTwo;
import com.shuzhi.led.entities.TStatusDto;
import com.shuzhi.light.entities.TLoopStateDto;
import com.shuzhi.service.DeviceLoopService;
import com.shuzhi.websocket.ApplicationContextUtils;
import lombok.Data;

/**
 * @author zgk
 * @description 设备信息
 * @date 2019-07-22 13:54
 */
@Data
public class Devices {

    /**
     * 设备id
     */
    private Long id;

    /**
     * 设备类型 1-顶棚照明；2-灯箱照明；3-logo照明；4-LED；5-LCD
     */
    private Integer type;

    /**
     * 状态：1-在线；0-离线
     */
    private Integer state;

    /**
     * 开关：1-开；0-关
     */
    private Integer onoff;

    /**
     * 亮度
     */
    private Integer light;

    /**
     * 音量
     */
    private Integer volume;

    /**
     * 时间
     */
    private String timestamp;

    /**
     * 封装lcd设备信息
     *
     * @param iotLcdStatus lcd设备信息
     */
    public Devices(IotLcdStatusTwo iotLcdStatus) {
        this.id = Long.valueOf(iotLcdStatus.getId());
        this.type = 5;
        this.state = Integer.valueOf(iotLcdStatus.getStatus());
        this.onoff = this.state;
        this.volume = iotLcdStatus.getVolume();
        this.timestamp = iotLcdStatus.getTimestamp();

    }

    /**
     * 封装led设备信息
     *
     * @param tStatusDto led设备信息
     */
    public Devices(TStatusDto tStatusDto) {

        this.id = Long.valueOf(tStatusDto.getId());
        this.type = 4;
        this.state = tStatusDto.getState();
        this.onoff = this.state;
        this.volume = tStatusDto.getVolume();
        this.light = tStatusDto.getLight();
        this.timestamp = tStatusDto.getTimestamp();
    }

    /**
     * 照明设备
     *
     * @param loopStateDto 照明设备信息
     */
    public Devices(TLoopStateDto loopStateDto) {

        //通过回路号查询这个是什么设备
        DeviceLoopService deviceLoopService = ApplicationContextUtils.get(DeviceLoopService.class);
        DeviceLoop deviceLoopSelect = new DeviceLoop();
        deviceLoopSelect.setLoop(loopStateDto.getLoop());
        deviceLoopSelect.setGatewayDid(loopStateDto.getGatewayId());
        DeviceLoop deviceLoop = deviceLoopService.selectOne(deviceLoopSelect);

        this.id = Long.valueOf(loopStateDto.getId());
        this.type = Integer.valueOf(deviceLoop.getTypecode());
        this.state = loopStateDto.getState();
        this.onoff = this.state;
        this.timestamp = loopStateDto.getTimestamp();
    }

}

