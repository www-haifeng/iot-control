package com.shuzhi.websocket.socketvo;

import com.shuzhi.lcd.entities.IotLcdStatusTwo;
import lombok.Data;

import java.util.List;

/**
 * @author zgk
 * @description
 * @date 2019-07-18 17:04
 */
@Data
public class Lcds{

    private List<IotLcdStatusTwo> allStatusByRedis;

}
