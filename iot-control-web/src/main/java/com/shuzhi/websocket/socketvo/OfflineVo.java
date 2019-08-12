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
