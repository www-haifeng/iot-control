package com.shuzhi.websocket.socketvo;

import lombok.Data;

/**
 * @author huliang
 * @date 2019/8/1 15:24
 */
@Data
public class OfflinesRingVo {
    /**
     * 设备id
     */
    private Integer id;
    /**
     * 名称
     */
    private String name;
    /**
     * 离线时间
     */
    private String offlinetime;
    /**
     * 状态;1-在线；0-离线
     */
    private Integer state;
}
