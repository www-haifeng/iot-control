package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.List;

/**
 * @author zgk
 * @description 照明首次连接数据
 * @date 2019-07-15 16:30
 */
@Data
public class LightMsg {

    private List<Lights> lights;

    public LightMsg(List<Lights> lights) {
        this.lights = lights;
    }
}
