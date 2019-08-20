package com.shuzhi.websocket.socketvo;

import com.shuzhi.lcd.entities.IotLcdStatusTwo;
import com.shuzhi.lcd.entities.TEventMessage;
import com.shuzhi.led.entities.TEventMessageLed;
import com.shuzhi.led.entities.TStatusDto;
import com.shuzhi.light.entities.TEvent;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zgk
 * @description 照明统计信息
 * @date 2019-07-31 10:32
 */
@Data
public class LightMsgVo {

    /**
     * 总数
     */
    private Integer total;

    /**
     * 在线
     */
    private Integer online;

    /**
     * 离线
     */
    private Integer offline;

    /**
     * 亮灯数
     */
    private Integer oncount;

    /**
     * 熄灯数
     */
    private Integer offcount;

    /**
     * 报警次数
     */
    private List<Lightalarms> lightalarms = new ArrayList<>();

    public LightMsgVo(StatisticsMsgVo statisticsMsgVo, List<TEvent> event) {

        this.total = statisticsMsgVo.getTotal();
        this.offcount = statisticsMsgVo.getOffcount();
        this.online = statisticsMsgVo.getOnline();
        this.oncount = statisticsMsgVo.getOncount();
        this.offline = statisticsMsgVo.getOffline();

        AtomicInteger order = new AtomicInteger(1);
        if (event != null){
            event.forEach(tEvent -> {
                Lightalarms lightalarm = new Lightalarms(tEvent.getCount(), tEvent.getCreateTime(), order.get());
                lightalarms.add(lightalarm);
                order.getAndIncrement();
            });
        }
    }

    public void lightMsgVoLcd(List<IotLcdStatusTwo> allStatusByRedis, List<TEventMessage> countByTime) {

        countByTime.forEach(tEventMessage -> lightalarms.add(new Lightalarms(tEventMessage)));
        this.online = Math.toIntExact(allStatusByRedis.stream().filter(iotLcdStatusTwo -> "1".equals(iotLcdStatusTwo.getStatus())).count());
        this.total = allStatusByRedis.size();
        this.offline = Math.toIntExact(allStatusByRedis.stream().filter(iotLcdStatusTwo -> "0".equals(iotLcdStatusTwo.getStatus())).count());

    }

    public void lightMsgVoLed(List<TStatusDto> allStatus, List<TEventMessageLed> countByTime) {
        if (countByTime != null){
            countByTime.forEach(tEventMessage -> lightalarms.add(new Lightalarms(tEventMessage)));
            this.online = Math.toIntExact(allStatus.stream().filter(tStatusDto -> tStatusDto.getOnline() == 1).count());
            this.total = allStatus.size();
            this.offline = Math.toIntExact(allStatus.stream().filter(tStatusDto -> tStatusDto.getOnline() == 0).count());
        }
    }

    public LightMsgVo() {
    }
}
