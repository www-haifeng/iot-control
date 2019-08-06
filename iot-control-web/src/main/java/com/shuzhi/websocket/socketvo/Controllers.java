package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.List;

/**
 * @author huliang
 * @date 2019/8/6 11:33
 */
@Data
public class Controllers {
    /**
     * 集中控制器id
     */
    private String controllerid;
    /**
     * 分组名称名称
     */
    private String controllername;
    /**
     * 集中控制器编码
     */
    private String controllernum;
    /**
     * 在线状态：1-在线；0-离线
     */
    private Integer state;

    private List<Loops> loopsList;
}
