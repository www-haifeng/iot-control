package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.List;

/**
 * @author hulinag
 * @description 照明首次连接数据
 * @date 2019-07-15 16:30
 */
@Data
public class LightMsg {

    private List<Loops> loops;

    public LightMsg(List<Loops> loops) {
        this.loops = loops;
    }
}
