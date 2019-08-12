package com.shuzhi.websocket.socketvo;

import com.shuzhi.led.entities.TStatusDto;
import lombok.Data;

import java.util.List;

/**
 * @author zgk
 * @description
 * @date 2019-07-15 15:52
 */
@Data
public class Leds {
    List<TStatusDto> leds;

    public Leds(List<TStatusDto> allStatus) {

    }
}
