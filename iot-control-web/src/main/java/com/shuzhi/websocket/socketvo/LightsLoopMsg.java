package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.List;


@Data
public class LightsLoopMsg {

    private  List<Loops> loops;

    public LightsLoopMsg(List<Loops> loops) {
        this.loops = loops;
    }

}
