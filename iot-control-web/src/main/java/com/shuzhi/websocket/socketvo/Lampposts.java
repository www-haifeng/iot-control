package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.List;

/**
 * @author huliang
 * @date 2019/8/7 9:31
 */
@Data
public class Lampposts {

    private Integer lamppostid;
    private String lamppostname;
    private List<Lights> lights;
}
