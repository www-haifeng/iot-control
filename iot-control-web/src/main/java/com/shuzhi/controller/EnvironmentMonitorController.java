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

import com.shuzhi.frt.entities.TData;
import com.shuzhi.frt.service.DataClientService;
import com.shuzhi.websocket.ApplicationContextUtils;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Array;
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


    /**
     * 根据Id查询
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
        if (id == 1) {
            List<Map<Object, Object>> list = dataClientService.findVew(id, deviceType, options, dateTime);
            Map<Object, List<Object>> divTopHashMap = new HashMap<>();
            List<Object> xList = new ArrayList<>();
            List<Object> yList = new ArrayList<>();
            for (Map<Object, Object> lt : list) {
                String dataTime = lt.get("datatime").toString();
                xList.add(dataTime);
                String num = lt.get("num").toString();
                yList.add(num);
            }
            divTopHashMap.put("xList", xList);
            divTopHashMap.put("yList", yList);
            objectMapHashMap.put("TOP", divTopHashMap);
        }
        // id 表示 left div
        if (id == 2) {
            List<Map<Object, Object>> list = dataClientService.findVew(id, deviceType, options, dateTime);
            Map<Object, List<Object>> divTopHashMap = new HashMap<>();
            List<Object> xList = new ArrayList<>();
            List<Object> yList = new ArrayList<>();
            for (Map<Object, Object> lt : list) {
                String dataTime = lt.get("datatime").toString();
                xList.add(dataTime);
                String num = lt.get("num").toString();
                yList.add(num);
            }
            divTopHashMap.put("xList", xList);
            divTopHashMap.put("yList", yList);
            objectMapHashMap.put("left", divTopHashMap);
        }
        // id 表示 right div
        if (id == 3) {
            List<Map<Object, Object>> list = dataClientService.findVew(id, deviceType, options, dateTime);
            Map<Object, List<Object>> divTopHashMap = new HashMap<>();
            List<Object> xList = new ArrayList<>();
            List<Object> yList = new ArrayList<>();
            for (Map<Object, Object> lt : list) {
                String dataTime = lt.get("datatime").toString();
                xList.add(dataTime);
                String num = lt.get("num").toString();
                yList.add(num);
            }
            divTopHashMap.put("xList", xList);
            divTopHashMap.put("yList", yList);
            objectMapHashMap.put("right", divTopHashMap);
        }

        return objectMapHashMap;
    }

}