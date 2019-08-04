package com.shuzhi.websocket.socketvo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zgk
 * @description
 * @date 2019-07-18 17:04
 */
@Data
@NoArgsConstructor
public class LcdMsgs {

    private List<Lcdss> lcds;

    public LcdMsgs(List<Lcdss> lcds) {
        this.lcds = lcds;
    }
}
