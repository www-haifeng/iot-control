package com.shuzhi.websocket.socketvo;

import lombok.Data;

/**
 * @author zgk
 * @description 接收控制消息
 * @date 2019-07-11 13:29
 */
@Data
public class MessageVo {

    /**
     * 报文消息id：UUID
     */
    private String msgid;

    /**
     * 模块编码
     */
    private Integer modulecode;

    /**
     * 消息类型
     */
    private Integer msgtype;

    /**
     * 消息编码
     */
    private Integer msgcode;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 报文数据信息
     */
    private Object msg;

}
