package com.shuzhi.entity;

import lombok.Data;

/**
 * @author huliang
 * @date 2019/8/18 11:39
 */
@Data
public class DeviceType {
    /**
     * 报警设备
     */
    private String deviceName;
    /**
     * 报警类型
     */
    private Integer type;
}
