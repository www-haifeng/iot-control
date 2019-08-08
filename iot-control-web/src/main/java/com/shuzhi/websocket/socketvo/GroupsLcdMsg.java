package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.List;

/**
 * @author huliang
 * @date 2019/8/6 9:39
 */
@Data
public class GroupsLcdMsg {
    private List<GroupsLcd> GroupsLcd;
    public GroupsLcdMsg(List<GroupsLcd> GroupsLcd) {
        this.GroupsLcd = GroupsLcd;
    }
}
