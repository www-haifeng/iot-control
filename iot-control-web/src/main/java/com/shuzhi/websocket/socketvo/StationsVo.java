package com.shuzhi.websocket.socketvo;

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

}
