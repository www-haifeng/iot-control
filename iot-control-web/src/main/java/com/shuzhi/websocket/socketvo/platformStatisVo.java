package com.shuzhi.websocket.socketvo;

import lombok.Data;

/**
 * @author zgk
 * @description
 * @date 2019-07-23 17:05
 */
@Data
public class platformStatisVo {

    /**
     * 照明总数
     */
    private Integer lighttotal = 0;

    /**
     * 照明在线
     */
    private Integer lightonline = 0;

    /**
     * 照明离线
     */
    private Integer lightoffline = 0;

    /**
     * led总数
     */
    private Integer ledtotal = 0;

    /**
     * led在线
     */
    private Integer ledonline = 0;

    /**
     * led离线
     */
    private Integer ledoffline = 0;

    /**
     * lcd总数
     */
    private Integer lcdtotal = 0;

    /**
     * lcd在线
     */
    private Integer lcdonline = 0;

    /**
     * lcd离线
     */
    private Integer lcdoffline = 0;

    /**
     * 本月
     */
    private float currentmonth = 0;

    /**
     * 上月
     */
    private float lastmonth = 0;

    /**
     * 本年
     */
    private float thisyear = 0;
}
