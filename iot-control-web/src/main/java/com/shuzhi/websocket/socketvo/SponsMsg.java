package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.List;

/**
 * @author huliang
 * @date 2019/8/8 9:50
 */
@Data
public class SponsMsg {
    private List<Spons> bcsts;

    public SponsMsg(List<Spons> bcsts) {
        this.bcsts = bcsts;
    }
}
