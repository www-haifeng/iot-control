package com.shuzhi.websocket.socketvo;

import lombok.Data;

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
}
