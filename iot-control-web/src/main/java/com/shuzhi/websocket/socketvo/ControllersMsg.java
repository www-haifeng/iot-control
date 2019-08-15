package com.shuzhi.websocket.socketvo;

import com.shuzhi.entity.GroupLightpoleDev;
import lombok.Data;

import java.util.List;

/**
 * @author hulinag
 * @description 集中控制器
 * @date 2019-07-15 16:30
 */
@Data
public class ControllersMsg {



    private  List<Controllers> controllers;

    public ControllersMsg(List<Controllers> controllers) {
        this.controllers = controllers;
    }

}
