package com.shuzhi.websocket.socketvo;

import lombok.Data;

/**
 * @author zgk
 * @description 报警次数
 * @date 2019-07-28 14:02
 */
@Data
public class LcdalarmsVo {

    /**
     * 序号
     */
    private Integer order;

    /**
     * 报警日期“2015-03-05”
     */
    private String date;

    /**
     * 报警次数
     */
    private Integer times;


}
