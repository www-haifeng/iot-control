package com.shuzhi.websocket;

import com.shuzhi.light.entities.StatisticsVo;
import com.shuzhi.light.entities.TElectricQuantity;
import com.shuzhi.light.service.LoopStatusServiceApi;
import com.shuzhi.websocket.socketvo.StatisticsMsgVo;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ProjectName: bus-station-web
 * @Package: com.shuzhi.websocket
 * @ClassName: Statistics
 * @Author: 陈鑫晖
 * @Date: 2019/7/15 15:12
 */
@Slf4j
public class Statistics {

    private static LoopStatusServiceApi loopStatusServiceApi;

    /**
     * 查询回路能耗
     *
     * @param statisticsVo
     * @return
     */
    public static StatisticsMsgVo findStatistics(StatisticsVo statisticsVo) throws ParseException {

        Optional.ofNullable(loopStatusServiceApi).orElseGet(() -> loopStatusServiceApi = ApplicationContextUtils.get(LoopStatusServiceApi.class));

        float activepowerNowMonth = 0;//本月能耗
        float activepowerLastMonth = 0;//上月能耗
        float activepowerYear = 0;//本年能耗
        float activepowerNow = 0;//当前最新能耗
        // HH:mm:ss
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //设置当前月份时间
        Date date = new Date();
        statisticsVo.setEndTime(sdf.format(date));
        //上个月第一天和上个月最后一天
        Map<String, String> map = Statistics.getFirstday_Lastday_Month(date);
        //取出上个月最后一天
        String day_last = map.get("last");
        Date date1 = sdf.parse(day_last);
        statisticsVo.setStartTime(sdf.format(date1));
        //获取本月能耗所有信息
        List<TElectricQuantity> electricQuantityNowMonth = loopStatusServiceApi.findElectricQuantity(statisticsVo);
        if(electricQuantityNowMonth == null || electricQuantityNowMonth.size()==0){
             activepowerNowMonth = 0.0f;
        }else {
            //获取最新能耗值
             activepowerNow = electricQuantityNowMonth.get(0).getActivepower();
            //获取上月最后一天能耗值
            float activepowerLastDay = electricQuantityNowMonth.get((electricQuantityNowMonth.size() - 1)).getActivepower();
            //本月能耗
            activepowerNowMonth = activepowerNow - activepowerLastDay;
        }
        //获取上月能耗
        //取出上个月第一天
        String day_first = map.get("first");
        Date date2 = sdf.parse(day_first);
        statisticsVo.setStartTime(sdf.format(date2));
        statisticsVo.setEndTime(sdf.format(date1));
        //获取上月月能耗所有信息
        List<TElectricQuantity> electricQuantityLastMonth = loopStatusServiceApi.findElectricQuantity(statisticsVo);
        if(electricQuantityLastMonth == null || electricQuantityLastMonth.size()==0){
            activepowerLastMonth = 0.0f;
        }else {
            //获取上月第一天能耗值
            float activepowerFirstDay = electricQuantityLastMonth.get(0).getActivepower();
            //获取上月最后一天能耗
            float activepowerLastDay1 = electricQuantityNowMonth.get((electricQuantityNowMonth.size() - 1)).getActivepower();
            //上月能耗
            activepowerLastMonth = activepowerLastDay1 - activepowerFirstDay;
        }
        //获取本年能耗
        //取出本年第一天
        String newYear = map.get("year");
        Date date3 = sdf.parse(newYear);
        statisticsVo.setStartTime(sdf.format(date3));
        statisticsVo.setEndTime(sdf.format(date));
        //获取上月月能耗所有信息
        List<TElectricQuantity> electricQuantityYear = loopStatusServiceApi.findElectricQuantity(statisticsVo);
        if( electricQuantityLastMonth == null || electricQuantityYear == null || electricQuantityYear.size() == 0){
            activepowerYear = 0.0f;
        }else {
            //获取本年第一天能耗值
            float activepowerFirstYearDay = electricQuantityYear.get(electricQuantityYear.size() - 1).getActivepower();
            //获取最新能耗值
            assert electricQuantityNowMonth != null;
            try {
                activepowerNow = electricQuantityNowMonth.get(0).getActivepower();
                //本年能耗
                activepowerYear = activepowerNow - activepowerFirstYearDay;
            }catch (Exception e){
                log.error("能耗统计发生错误 : {} ", e.getMessage());
            }

        }
        return new StatisticsMsgVo(activepowerNowMonth, activepowerLastMonth, activepowerYear);
    }


    /**
     * 根据当前时间得到上个月的第一天和最后一天
     * @param date
     * @return
     */
    private static Map<String, String> getFirstday_Lastday_Month(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        Date theDate = calendar.getTime();

        // 上个月第一天  
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(day_first).append(" 00:00:00");
        day_first = str.toString();
        // 上个月最后一天  
        calendar.add(Calendar.MONTH, 1);// 加一个月  
        calendar.set(Calendar.DATE, 1);// 设置为该月第一天  
        calendar.add(Calendar.DATE, -1);// 再减一天即为上个月最后一天  
        String day_last = df.format(calendar.getTime());
        StringBuffer endStr = new StringBuffer().append(day_last).append(" 23:59:59");
        day_last = endStr.toString();

        //获取本年第一天
        String year = new SimpleDateFormat("yyyy").format(date);
        StringBuffer yearFirstDay = new StringBuffer().append(year).append("-01-01 00:00:00");
        String newYear = yearFirstDay.toString();

        Map<String, String> map = new HashMap<String, String>();
        map.put("first", day_first);
        map.put("last", day_last);
        map.put("year", newYear);
        return map;
    }

}
