package com.shuzhi.websocket.socketvo;

import lombok.Data;

/**
 * @author zgk
 * @description 封装简易协议
 * @date 2019-07-12 11:15
 */
@Data
public class SimpleProtocolVo {

    /**
     * 唯一标识
     */
    private String msgid;

    /**
     * 对应lights
     */
    private String did;

    /**
     * 返回值的状态码
     */
    private Integer code;

    /**
     * 命令id，统一分配
     */
    private String cmdid;

    /**
     * 命令参数
     */
    private Object data;


}
