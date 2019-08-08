package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.List;

/**
 * @author huliang
 * @date 2019/8/5 10:02
 */
@Data
public class Loops {
    private Integer loopid;
    private Integer loopnum;
    private String loopname;
    private Integer state;
    private Integer onoff;
    List<Lampposts> lampposts;

}
