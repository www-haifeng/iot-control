package com.shuzhi.websocket.socketvo;

import lombok.Data;

/**
 * @author zgk
 * @description
 * @date 2019-07-18 17:04
 */
@Data
public class LcdMsg {

    private Lcds lcds;

    public LcdMsg(Lcds lcds) {
        this.lcds = lcds;
    }
}
