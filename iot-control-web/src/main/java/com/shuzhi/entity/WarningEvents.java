package com.shuzhi.entity;

import lombok.Data;

/**
 * @author huliang
 * @date 2019/8/18 11:28
 */
@Data
public class WarningEvents {
    /**
     * 编号
     */
    private Integer id;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 报警类型 201-离线报警；202-在线报警 事件类型 101-开；102-关；103-在线；104-离线;
     */
    private String type;
    /**
     * 报警时间
     */
    private String WarningTime;
}
