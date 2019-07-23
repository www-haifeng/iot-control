package com.shuzhi.websocket.socketvo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author zgk
 * @description 报文数据
 * @date 2019-07-11 13:32
 */
@Data
public class Msg {

    /**
     * 命令类型：1-开启；2-关闭；3-重启；4-调光；5-音量
     */
    private Integer cmdtype;

    /**
     * 调光值(lcd不能调光)
     */
    private Integer light;

    /**
     * 音量值
     */
    private Integer volume;

    /**
     * lcd数组 屏id
     */
    private List<String> lcds;

    /**
     * led数组 屏id
     */
    private List<String> leds;

    /**
     * led数组 屏id
     */
    private List<String> lights;


}
