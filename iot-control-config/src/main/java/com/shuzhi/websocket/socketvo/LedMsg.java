package com.shuzhi.websocket.socketvo;

import lombok.Data;

/**
 * @author zgk
 * @description led 首次建立连接的返回信息
 * @date 2019-07-15 15:48
 */
@Data
public class LedMsg {
    private Leds leds;

    public LedMsg(Leds leds) {
        this.leds = leds;
    }
}
