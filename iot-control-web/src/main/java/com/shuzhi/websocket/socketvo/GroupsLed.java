package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.List;

/**
 * @author huliang
 * @date 2019/8/6 9:39
 */
@Data
public class GroupsLed {
    /**
     * 	分组id
     */
    private Integer groupid;
    /**
     * 分组名称名称
     */
    private String groupname;

    private List<Ledss> Leds;
}
