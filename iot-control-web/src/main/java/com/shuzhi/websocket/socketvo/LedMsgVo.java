package com.shuzhi.websocket.socketvo;

import com.shuzhi.lcd.entities.IotLcdStatusTwo;
import com.shuzhi.lcd.entities.TEventMessage;
import com.shuzhi.led.entities.TEventMessageLed;
import com.shuzhi.led.entities.TStatusDto;
import com.shuzhi.light.entities.TEvent;
import com.shuzhi.lightiotcomm.entities.ControllerApi;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zgk
 * @description Led统计信息
 * @date 2019-07-31 10:32
 */
@Data
public class LedMsgVo {

    /**
     * 总数
     */
    private Integer total =0;

    /**
     * 在线
     */
    private Integer online =0;

    /**
     * 离线
     */
    private Integer offline =0;


    /**
     * 报警次数
     */
    private List<Ledalarms> ledalarms = new ArrayList<>();


    public void ledMsgVoLed(List<TStatusDto> allStatus, List<TEventMessageLed> countByTime) {

        this.total = allStatus.size();
        for (TStatusDto tStatusDto:allStatus) {
            if(tStatusDto.getOnline() != 0){
                online ++;
            }else{
                offline ++;
            }
        }
        for (TEventMessageLed led:countByTime) {
            Ledalarms ledalarms1 = new Ledalarms(led);
            ledalarms.add(ledalarms1);
        }

    }




}
