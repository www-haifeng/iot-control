package com.shuzhi.entity;

import lombok.Data;

/**
 * @author huliang
 * @date 2019/8/18 11:28
 */
@Data
public class WarningEventsVo {
    /**
     * 开始时间
     */
    private String startTime;
    /**
     *结束事件
     */
    private String endTime;
    /**
     * 报警设备
     */
    private Integer deviceName;
    /**
     * 报警类型
     */
    private String type;
    /**
     * 设备编号
     */
    private String deviceNumber;
    /**
     * 分页页数
     */
    private Integer pageNumber;
    /**
     * 条数
     */
    private Integer pageSize;

}
