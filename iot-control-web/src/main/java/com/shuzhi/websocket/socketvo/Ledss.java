package com.shuzhi.websocket.socketvo;


import com.shuzhi.led.entities.TStatusDto;
import lombok.Data;

/**
 * @author zgk
 * @description
 * @date 2019-07-18 17:04
 */
@Data
public class Ledss extends TStatusDto {

    private Integer lamppostid;
    private String lamppostname;

}
