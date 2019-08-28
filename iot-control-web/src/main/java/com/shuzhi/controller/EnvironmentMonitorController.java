/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: EnvironmentMonitorController
 * Author:   hp
 * Date:     2019/8/15 16:02
 * Description: 環境監測
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.shuzhi.controller;

import com.shuzhi.frt.service.DataClientService;
import com.shuzhi.frt.service.TEventService;
import com.shuzhi.led.service.TEventLedService;
import com.shuzhi.lightiotcomm.service.LightIotCommServiceApi;
import com.shuzhi.websocket.ApplicationContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 〈環境監測〉<br>
 *
 * @author guobin
 * @create 2019/8/15
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/environmentmonitor")
public class EnvironmentMonitorController {

    @Autowired
    private DataClientService dataClientService;
    @Autowired
    private TEventService tEventService;
    @Autowired
    private TEventLedService tEventLedService;

    @Autowired
    private LightIotCommServiceApi lightiotcommserviceapi;

    /**
     * 根据Id查询 <环测监控>
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}/", method = RequestMethod.GET)
    @ResponseBody
    public Map<Object, Map<Object, List<Object>>> findView(@PathVariable("id") Integer id,
                                                           @RequestParam(value = "deviceType", required = false) String deviceType,
                                                           @RequestParam(value = "options", required = false) String options,
                                                           @RequestParam(value = "dateTime", required = false) String dateTime) {
        Optional.ofNullable(dataClientService).orElseGet(() -> dataClientService = ApplicationContextUtils.get(DataClientService.class));
        // id 表示 top div //TODO dataClientService.findVew(id,deviceType,options,dateTime);
        Map<Object, Map<Object, List<Object>>> objectMapHashMap = new HashMap<>();
        List<Map<Object, Object>> list = dataClientService.findVew(id, deviceType, options, dateTime);
        Map<Object, List<Object>> divHashMap = new HashMap<>();
        List<Object> xList = new ArrayList<>();
        List<Object> yList = new ArrayList<>();
        List<Object> zList = new ArrayList<>();
        if (id == 1) {
            //初始化页面格式的数据,初始化容器
            List<Map<Object, Object>> initList = null;
            int index = 23;
            if ("winddirection".equalsIgnoreCase(options)) {
                initList = creatMap(index, dateTime, "datatime", "windspeed", "winddirection");
            } else initList = creatMap(index, dateTime, "datatime", "num");
            for (Map<Object, Object> li : initList) {
                if ("winddirection".equalsIgnoreCase(options)) {
                    for (Map<Object, Object> lt : list) {
                        if (li.get("datatime").toString().equals(lt.get("datatime"))) {
                            String windspeed = lt.get("windspeed").toString();
                            li.put("windspeed", windspeed);
                            String winddirection = lt.get("winddirection").toString();
                            li.put("winddirection", winddirection);
                        }
                    }
                    String dataTime = li.get("datatime").toString();
                    xList.add(dataTime);
                    String windspeed = li.get("windspeed").toString();
                    yList.add(windspeed);
                    String winddirection = li.get("winddirection").toString();
                    zList.add(winddirection);

                } else {
                    for (Map<Object, Object> lt : list) {
                        if (li.get("datatime").toString().equals(lt.get("datatime"))) {
                            String num = lt.get("num").toString();
                            li.put("num", num);
                        }
                    }
                    String dataTime = li.get("datatime").toString();
                    xList.add(dataTime);
                    String windspeed = li.get("num").toString();
                    yList.add(windspeed);
                }
            }
            divHashMap.put("xList", xList);
            divHashMap.put("yList", yList);
            divHashMap.put("zList", zList);
            objectMapHashMap.put("TOP", divHashMap);
        }
        // id 表示 left div
        if (id == 2) {
            List<Map<Object, Object>> initList = new ArrayList<>();
            Map<Object, Object> map = new HashMap<>();
            map.put('优', '0');
            map.put('良', '0');
            map.put("轻度污染", '0');
            map.put("中度污染", '0');
            map.put("严重污染", '0');
            initList.add(map);
            for (Object init : map.keySet()) {
                for (Map<Object, Object> lt : list) {
                    if (init.toString().equalsIgnoreCase(lt.get("datatime").toString())) {
                        String num = lt.get("num").toString();
                        map.put(init, num);
                    }
                }
                xList.add(init.toString());
                yList.add(map.get(init).toString());
            }
            divHashMap.put("xList", xList);
            divHashMap.put("yList", yList);
            objectMapHashMap.put("left", divHashMap);
        }
        // id 表示 right div
        if (id == 3) {
            // 获取当前小时
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.get(Calendar.HOUR_OF_DAY);
            String suffix = new SimpleDateFormat("yyyy").format(calendar.getTime());
            List<Map<Object, Object>> list1 = initMapByMonth(Integer.valueOf(suffix), Integer.valueOf(dateTime));
            for (Map<Object, Object> lt1 : list1) {
                for (Map<Object, Object> lt : list) {
                    if (lt1.get("datatime").toString().equalsIgnoreCase(lt.get("datatime").toString().substring(8, 10))) {
                        String num = lt.get("num").toString();
                        lt1.put("num", num);
                    }
                }
                String dataTime = lt1.get("datatime").toString();
                xList.add(dataTime);
                String num = lt1.get("num").toString();
                yList.add(num);
            }

            divHashMap.put("xList", xList);
            divHashMap.put("yList", yList);
            objectMapHashMap.put("right", divHashMap);
        }

        return objectMapHashMap;
    }

    private List<Map<Object, Object>> initMapByMonth(Integer year, Integer month) {
        List<Map<Object, Object>> initMaps = new ArrayList<>();
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        //初始化容器
        for (int i = 1; i <= maxDate; i++) {
            Map<Object, Object> mapDate = new HashMap<>();
            if (i < 10) {
                mapDate.put("datatime", "0" + i);
                mapDate.put("num", "0");
                initMaps.add(mapDate);
            } else {
                mapDate.put("datatime", i);
                mapDate.put("num", "0");
                initMaps.add(mapDate);
            }

        }
        return initMaps;
    }

    private List<Map<Object, Object>> creatMap(int index, String prefixDate, String... args) {
        //初始化页面格式的数据
        List<Map<Object, Object>> initMaps = new ArrayList<>();
        // 获取当前小时
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.get(Calendar.HOUR_OF_DAY);
        String suffix = new SimpleDateFormat("HH").format(calendar.getTime());

        //获取indexDate的前后index小时
        Date indexDate = null;
        try {
            Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(prefixDate);
            String prefix = new SimpleDateFormat("yyyy-MM-dd ").format(parse);
            String tempDate = prefix + suffix;
            indexDate = new SimpleDateFormat("yyyy-MM-dd HH", Locale.CHINA).parse(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("-------------日期格式转换异常-----------------");

        }
        //初始化容器
        for (int i = index; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(indexDate);//转化日历类型
            cal.add(Calendar.HOUR_OF_DAY, -i);
//            cal.set(cal.HOUR_OF_DAY, cal.get(cal.HOUR_OF_DAY) - i );
            Date date = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH", Locale.CHINA);
            String dateString = sdf.format(date);
            Map<Object, Object> mapDate = new HashMap<>();
            for (String arg : args) {
                if ("datatime".equalsIgnoreCase(arg)) {
                    mapDate.put(arg, dateString);
                } else if ("winddirection".equalsIgnoreCase(arg)) {
                    mapDate.put(arg, 0);
                } else if ("windspeed".equalsIgnoreCase(arg)) {
                    mapDate.put(arg, 0);
                } else mapDate.put(arg, 0);
            }
            initMaps.add(mapDate);
        }
        return initMaps;

    }


    /**
     * 根据Id查询 <报警信息>
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/alarmAnalysis/", method = RequestMethod.GET)
    @ResponseBody
    public Map<Object, Map<Object, List<Object>>> alarmAnalysis(@RequestParam("id") String id,
                                                                @RequestParam(value = "lastMonth", required = false) String lastMonth,
                                                                @RequestParam(value = "thisMonth", required = false) String thisMonth,
                                                                @RequestParam(value = "startTime", required = false) String startTime,
                                                                @RequestParam(value = "endTime", required = false) String endTime) {
        Optional.ofNullable(dataClientService).orElseGet(() -> dataClientService = ApplicationContextUtils.get(DataClientService.class));
        Map<Object, Map<Object, List<Object>>> objectMapHashMap = new HashMap<>();
        //环测线率 或者 天数离线次数按天 或者告警次数 或者 按天数统计告警
        // (id=1 为离线率 id=2为当天使用次数 id=3 为告警次数 4为按天统计告警次数)
        List<Map<Object, Object>> frtOnLine = tEventService.findFrtOnLine(id, lastMonth, thisMonth, startTime, endTime);
        //LED线率 或者 天数离线次数按天 或者告警次数 或者 按天数统计告警
        // (id=1 为离线率 id=2为当天使用次数 id=3 为告警次数 4为按天统计告警次数)
        List<Map<Object, Object>> ledOnLine = tEventLedService.findLEDOnLine(id, lastMonth, thisMonth, startTime, endTime);
        //照明线率 或者 天数离线次数按天 或者告警次数 或者 按天数统计告警
        // (仅有id=3 为告警次数 4为按天统计告警次数)
        List<Map<Object, Object>> lightOnLine = lightiotcommserviceapi.findLightOnLine(id, lastMonth, thisMonth, startTime, endTime);
        Map<Object, List<Object>> Map = new HashMap<>();
        List<Object> xList = new ArrayList<>();
        List<Object> yList = new ArrayList<>();
        //页面初始化查询 当前时间一个月的数据
        /*****************************************第二个页面数据接口已经写完了只需要在容器中拼接显示就可以****************************************************************/
        if ("top".equalsIgnoreCase(id)) {
            //查询区间数据
            if (startTime.isEmpty() && endTime.isEmpty()) {
//                if ("1".equalsIgnoreCase(id)) {
//                    Object num = frtOnLine.get("num");
//                    xList.add("环境监测");
//                    yList.add(num);
//                    Map.put("xList", xList);
//                    Map.put("yList", yList);
//                    objectMapHashMap.put("TOPFrtLeft", Map);
//                    xList.clear();
//                    yList.clear();
//                    Map.clear();
//                    xList.add("LED屏");
//                    Object ledNum = ledOnLine.get("num");
//                    yList.add(ledNum);
//                    Map.put("xList", xList);
//                    Map.put("yList", yList);
//                    objectMapHashMap.put("TOPLEDLeft", Map);
//                    xList.clear();
//                    yList.clear();
//                    Map.clear();
//                }

                if ("2".equalsIgnoreCase(id)) {
                    // 初始化页面元素
                    List<java.util.Map<Object, Object>> list = null;
                    if ("lastMonth".equalsIgnoreCase(lastMonth)) {
                        list = iniTDateMap("lastMonth");
                        for (Map<Object, Object> lt1 : list) {
//                            for (Object lt : frtOnLine.keySet()) {
//                                if (lt1.get("datatime").toString().equalsIgnoreCase(lt.toString())) {
//                                    String num = lt.get("num").toString();
//                                    lt1.put("num", num);
//                                }
//                            }
                            String dataTime = lt1.get("datatime").toString();
                            xList.add(dataTime);
                            String num = lt1.get("num").toString();
                            yList.add(num);
                        }
                    }
                    if ("thisMonth".equalsIgnoreCase(thisMonth)) {
                        list = iniTDateMap("lastMonth");
                    }
                }


            }
            //查询上月后下月的数据
            if (!lastMonth.isEmpty() || !thisMonth.isEmpty()) {

            }
        }

        return objectMapHashMap;
    }

    private List<Map<Object, Object>> iniTDateMap(String... args) {
//        String id, String lastMonth, String thisMonth, String startTime, String endTime
        Date start = null;
        Date end = null;
        for (String arg : args) {
            try {
                if ("lastMonth".equalsIgnoreCase(arg)) {
                    start = getFirstDayOfMonth1("lastMonth");
                    end = getLastDayOfMonth1("lastMonth");
                } else if ("thisMonth".equalsIgnoreCase(arg)) {
                    start = getFirstDayOfMonth1("thisMonth");
                    end = getLastDayOfMonth1("thisMonth");
                } else {
                    if ("startTime".equalsIgnoreCase(arg)) {
                        start = new SimpleDateFormat("yyyy-MM-dd").parse(arg);
                    }
                    if ("endTime".equalsIgnoreCase(arg)) {
                        end = new SimpleDateFormat("yyyy-MM-dd").parse(arg);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        //初始化页面格式的数据
        List<Map<Object, Object>> result = new ArrayList<>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);
        tempStart.add(Calendar.DAY_OF_YEAR, 1);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        while (tempStart.before(tempEnd)) {
            HashMap<Object, Object> temp = new HashMap<>();
            temp.put(new SimpleDateFormat("yyyy-MM-dd").format(tempStart.getTime()),"0");
            result.add(temp);
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    private Date getFirstDayOfMonth1(String args) {
        // 获取当月第一天和最后一天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cale = null;
        // 获取前月的第一天
        cale = Calendar.getInstance();
        if ("lastMonth".equalsIgnoreCase(args)) {
            cale.add(Calendar.MONTH, -1);
        }
        if ("thisMonth".equalsIgnoreCase(args)) {
            cale.add(Calendar.MONTH, 0);
        }
        cale.set(Calendar.DAY_OF_MONTH, 1);
        return cale.getTime();


    }


    private Date getLastDayOfMonth1(String args) {
        // 获取当月第一天和最后一天
        Calendar cale = null;
        // 获取前月的第一天
        cale = Calendar.getInstance();
        if ("lastMonth".equalsIgnoreCase(args)) {
            cale.add(Calendar.MONTH, -1);
        }
        if ("thisMonth".equalsIgnoreCase(args)) {
            cale.add(Calendar.MONTH, 0);
        }

        cale.set(Calendar.DAY_OF_MONTH, 0);
        return cale.getTime();
    }
}