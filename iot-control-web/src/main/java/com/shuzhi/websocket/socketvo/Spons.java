package com.shuzhi.websocket.socketvo;

import com.shuzhi.spon.entitys.Bcsts;
import lombok.Data;

/**
 * @author huliang
 * @date 2019/8/8 9:50
 */
@Data
public class Spons extends Bcsts {
    /**
     * 	分组id
     */
    private Integer groupid;
    /**
     * 分组名称名称
     */
    private String groupname;
    /**
     * 灯杆id
     */
    private Integer lamppostid;
    /**
     * 灯杆名称
     */
    private String lamppostname;
}
