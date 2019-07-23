
package com.shuzhi.rabbitmq;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @Program: 封装json
 * @Description:
 * @Author: YuJQ
 * @Create: 2019/6/5 19:15
 **/
@Data
public class Message implements Serializable {
    //第一层结构

    /**
     * 报文消息id
     */
    private String msgid;

    /**
     * 消息类型：0:心跳 1：命令 2：回执 3：命令执行结果 4：上报信息 5：首次建立连接 6、建连回复 7、告警信息
     */
    private String msgtype;

    /**
     * 消息编码
     */
    private Integer msgcode;

    /**
     * 系统类型 应用网关 使能平台
     */
    private String systype;

    /**
     * 系统id
     */
    private String sysid;
    /**
     * 链路id
     */
    private String connectid;

    /**
     * sign = sha(msgid+key+MD5(msg)+ msgts)
     */
    private String sign;

    /**
     * 模块编码
     */
    private Integer modulecode;

    /**
     * 时间戳 “2015-03-05 17:59:00.567”
     */
    private String msgts;

    /**
     * 报文数据信息
     */
    private Object msg;

    //第二层结构
    /**
     * "timestamp": "2015-03-05 17:59:00"
     */
    private String timestamp;

    /**
     * 建连信息状态码
     */
    private String code;

    /**
     * 心跳包上传周期，默认30秒
     */
    private String cycletime;

    /**
     * 命令下发超时时间
     */
    private String overtime;

    /**
     * 设备类型
     */
    private String type;

    /**
     * 厂商类型
     */
    private String subtype;

    /**
     * 集中控制器编号
     */
    private String did;

    /**
     * 命令id，统一分配
     */
    private String cmdid;

    /**
     * 告警id,暂未定义，统一分配
     */
    private String alarmid;

    //第三层结构

    private Object data;


}
