package com.shuzhi.websocket.socketvo;

import com.shuzhi.light.entities.TLoopStateDto;
import com.shuzhi.lightiotcomm.entities.ControllerApi;
import com.shuzhi.lightiotcomm.entities.LoopRedis;
import com.shuzhi.lightiotcomm.entities.TControllerState;
import lombok.Data;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @ProjectName: bus-station-web
 * @Package: com.shuzhi.websocket.socketvo
 * @ClassName: StatisticsMsgVo统计能耗实体类
 * @Author: 陈鑫晖
 * @Date: 2019/7/19 14:57
 */
@Data
public class StatisticsMsgsVo {

    /**
     * 亮灯率
     */
    private String lightrate;

    /**
     * 在线率
     */
    private String onlinerate;

    /**
     * 总数
     */
    private double total = 0;

    /**
     * 亮灯数
     */
    private double onnum = 0;

    /**
     * 熄灯数
     */
    private double offnum = 0;

    /**
     * 故障数
     */
    private double errornum = 0;

    public void addLightNum(List<ControllerApi> controllerStatus) {
        double onlin = 0;
        this.total = controllerStatus.size();
        for (ControllerApi tStatusDto:controllerStatus) {
            if(tStatusDto.getOnoff() == 1){
                this.onnum++;
            }else if(tStatusDto.getOnoff() == 4){
                this.errornum ++;
            }else{
                this.offnum++;
            }
            if(tStatusDto.getOnline() != 0){
                onlin ++;
            }
        }

        //this.onnum = Math.toIntExact(controllerStatus.stream().filter(tStatusDto -> tStatusDto.getOnoff() == 1 ).count());
        //this.offnum = Math.toIntExact(controllerStatus.stream().filter(tStatusDto -> tStatusDto.getOnoff() == 2 ).count());
        //double onlin = Math.toIntExact(controllerStatus.stream().filter(tStatusDto -> tStatusDto.getOnline() != 0 ).count());
        //this.errornum = Math.toIntExact(controllerStatus.stream().filter(tStatusDto -> tStatusDto.getOnoff() == 4 ).count());
        //this.onlinerate = String.valueOf(onnum / total * 100).split("\\.")[0];
        //this.lightrate = String.valueOf(onlin / total * 100).split("\\.")[0];
        this.onlinerate = division(onnum,total ) * 100 +"";
        this.lightrate = division(onlin,total )* 100 +"";
    }

    public static double division(Double arg1,Double arg2) {
        DecimalFormat df=new DecimalFormat("0.000");//设置保留3位数

       return Double.parseDouble(df.format((double) arg1/arg2));

    }
}
