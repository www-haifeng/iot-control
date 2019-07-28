package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zgk
 * @description webSocket回执信息
 * @date 2019-07-11 10:21
 */
@Data
public class ReceiptHandleVo {

    /**
     * 报文消息id：UUID
     */
    private String msgid;

    /**
     * 模块编码
     */
    private Integer modulecode;

    /**
     * 时间戳
     */
    private String timestamp;

    public ReceiptHandleVo(String msgid, Integer modulecode, Date timestamp) {
        this.msgid = msgid;
        this.modulecode = modulecode;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(timestamp);
    }

    public void setTimestamp(Date timestamp) {
       this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(timestamp);


    }
}
