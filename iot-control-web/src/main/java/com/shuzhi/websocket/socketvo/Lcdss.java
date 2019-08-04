package com.shuzhi.websocket.socketvo;


import com.shuzhi.lcd.entities.IotLcdStatusTwo;
import lombok.Data;

/**
 * @author zgk
 * @description
 * @date 2019-07-18 17:04
 */
@Data
public class Lcdss extends IotLcdStatusTwo{

    private Integer lamppostid;
    private String lamppostname;

}
