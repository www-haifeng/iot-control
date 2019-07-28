package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.Date;

/**
 * @ProjectName: bus-station-web
 * @Package: com.shuzhi.websocket.socketvo
 * @ClassName: StatisticsVo
 * @Author: 陈鑫晖
 * @Date: 2019/7/16 10:25
 */
@Data
public class StatisticsVo {

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 集中控制器id
     */
    private String did;

    /**
     * 回路号id
     */
    private Integer hid;
}
