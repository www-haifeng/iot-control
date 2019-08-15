package com.shuzhi.websocket;

import com.shuzhi.websocket.socketvo.Lampposts;
import lombok.Data;

import java.util.List;

/**
* @Program: 集中控制器回路
* @Description: 
* @Author: YuJQ
* @Create: 2019/8/15 15:20
**/
@Data
public class GateWayLoops {
    private Integer id;
    private Integer loopnum;
    private String name;

    private Integer onoff;

}
