package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.List;

/**
 * @author zgk
 * @description 照明设备状态
 * @date 2019-07-22 10:22
 */
@Data
public class LightMsgState {

    /**
     * 灯箱设备
     */
    private List<Lights> lamphouses;

    /**
     * 顶棚
     */
    private List<Lights> platfonds;

    /**
     * log
     */
    private List<Lights> logos;



}
