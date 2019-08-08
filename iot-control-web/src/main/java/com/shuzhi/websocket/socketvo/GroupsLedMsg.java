package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.List;

/**
 * @author huliang
 * @date 2019/8/6 9:39
 */
@Data
public class GroupsLedMsg {
    private List<GroupsLed> Groups;
    public GroupsLedMsg(List<GroupsLed> Groups) {
        this.Groups = Groups;
    }
}
