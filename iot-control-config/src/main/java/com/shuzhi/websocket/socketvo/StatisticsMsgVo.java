package com.shuzhi.websocket.socketvo;

import lombok.Data;

/**
 * @ProjectName: bus-station-web
 * @Package: com.shuzhi.websocket.socketvo
 * @ClassName: StatisticsMsgVo统计能耗实体类
 * @Author: 陈鑫晖
 * @Date: 2019/7/19 14:57
 */
@Data
public class StatisticsMsgVo {

    /**
     * 总数
     */
    private Integer total;

    /**
     * 在线
     */
    private Integer online;

    /**
     * 离线
     */
    private Integer offline;

    /**
     * 亮灯数
     */
    private Integer oncount;

    /**
     * 熄灯数
     */
    private Integer offcount;

    /**
     * 本月
     */
    private float currentmonth;

    /**
     * 上月
     */
    private float lastmonth;

    /**
     * 本年
     */
    private float thisyear;


    public StatisticsMsgVo(float currentmonth, float lastmonth, float thisyear) {
        this.currentmonth = currentmonth;
        this.lastmonth = lastmonth;
        this.thisyear = thisyear;
    }
    public StatisticsMsgVo() {
    }
}
