package com.shuzhi.websocket.socketvo;

import com.shuzhi.lcd.entities.TEventMessage;
import lombok.Data;

/**
 * @author zgk
 * @description 报警次数
 * @date 2019-07-31 10:28
 */
@Data
public class Lightalarms {

    /**
     * 序号
     */
    private Integer order;

    /**
     * 报警日期
     */
    private String date;

    /**
     * 报警次数
     */
    private Integer times;


    Lightalarms(Integer count, String createtime, int i) {

        this.date = createtime;
        this.order = i;
        this.times = count;

    }

    Lightalarms(TEventMessage tEventMessage) {

        this.date = tEventMessage.getAlarmtime();
        this.order = tEventMessage.getOrder();
        this.times = tEventMessage.getTimes();

    }
}
