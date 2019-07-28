package com.shuzhi.websocket.socketvo;

import com.shuzhi.entity.DeviceLoop;
import com.shuzhi.entity.DeviceStation;
import com.shuzhi.light.entities.TLoopStateDto;
import com.shuzhi.service.DeviceLoopService;
import com.shuzhi.service.DeviceStationService;
import com.shuzhi.service.StationService;
import com.shuzhi.websocket.ApplicationContextUtils;
import lombok.Data;

/**
 * @author zgk
 * @description
 * @date 2019-07-15 16:32
 */
@Data
public class Lights {

    /**
     * 公交站id
     */
    private Long stationid;

    /**
     * 公交站名称
     */
    private String stationname;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 灯箱id
     */
    private Long lamphouseid;

    /**
     * 灯箱开关
     */
    private Integer lamphouseonoff;

    /**
     * 灯箱在线
     */
    private Long lamphouseline;

    /**
     * 顶棚id
     */
    private Long platfondid;

    /**
     * 顶棚开关
     */
    private Integer platfondonoff;

    /**
     * 顶棚在线
     */
    private Long platfondline;

    /**
     * logoid
     */
    private Long logoid;

    /**
     * logo开关
     */
    private Integer logoonoff;

    /**
     * logo在线
     */
    private Long logoline;

    /**
     * id
     */
    private Long id;


    public Lights(TLoopStateDto tLoopStateDto) {

        //通过回路号查询这个是什么设备
        DeviceLoopService deviceLoopService = ApplicationContextUtils.get(DeviceLoopService.class);
        DeviceStationService deviceStationService = ApplicationContextUtils.get(DeviceStationService.class);
        DeviceLoop deviceLoopSelect = new DeviceLoop();
        deviceLoopSelect.setLoop(tLoopStateDto.getLoop());
        DeviceLoop deviceLoop = deviceLoopService.selectOne(deviceLoopSelect);
        //查出 对应的公交站id和名称
        DeviceStation deviceStationSelect = new DeviceStation(String.valueOf(deviceLoop.getDeviceDid()));
        DeviceStation deviceStation = deviceStationService.selectOne(deviceStationSelect);
        StationService stationService = ApplicationContextUtils.get(StationService.class);
        if (deviceStation != null){
            this.stationid = Long.valueOf(deviceStation.getStationid());
            this.stationname = stationService.selectByPrimaryKey(deviceStation.getStationid()).getStationName();
        }
        //判断这是什么设备
        switch (deviceLoop.getTypecode()){
            //灯箱
            case "3" :
                this.lamphouseid = Long.valueOf(deviceLoop.getDeviceDid());
                this.lamphouseonoff = tLoopStateDto.getState();
                this.lamphouseline = Long.valueOf(tLoopStateDto.getState());

                break;
            //顶棚
            case "1" :
                this.platfondid = Long.valueOf(deviceLoop.getDeviceDid());
                this.platfondline = Long.valueOf(tLoopStateDto.getState());
                this.platfondonoff = tLoopStateDto.getState();
                break;
            //log
            case "2" :
                this.logoid = Long.valueOf(deviceLoop.getDeviceDid());
                this.logoline = Long.valueOf(tLoopStateDto.getState());
                this.logoonoff = tLoopStateDto.getState();
            default:
        }
        this.name = deviceLoop.getDeviceName();
        this.id = Long.valueOf(deviceLoop.getDeviceDid());
        //this.stationid = Integer.valueOf(tLoopStateDto.getGatewayId());
    }
}
