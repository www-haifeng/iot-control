package com.shuzhi.websocket.socketvo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zgk
 * @description led 首次建立连接的返回信息
 * @date 2019-07-15 15:48
 */
@Data
@NoArgsConstructor
public class LedMsgs {
    private List<Ledss> leds;

    public LedMsgs(List<Ledss> leds) {
        this.leds = leds;
    }
}
