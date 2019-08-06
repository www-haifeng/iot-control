package com.shuzhi.websocket;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuzhi.entity.*;
import com.shuzhi.frt.entities.OfflinesRingVo;
import com.shuzhi.frt.entities.StatisticalPoleVo;
import com.shuzhi.frt.entities.TDataDto;
import com.shuzhi.frt.service.DataClientService;
import com.shuzhi.frt.service.DeviceClientService;
import com.shuzhi.lcd.entities.IotLcdStatusTwo;
import com.shuzhi.lcd.service.IotLcdsStatusService;
import com.shuzhi.lcd.service.TEventLcdService;
import com.shuzhi.led.entities.TStatusDto;
import com.shuzhi.led.service.TEventLedService;
import com.shuzhi.led.service.TStatusService;
import com.shuzhi.light.entities.GatewayRedis;
import com.shuzhi.light.entities.StatisticsVo;
import com.shuzhi.light.entities.TLoopStateDto;
import com.shuzhi.light.service.LoopStatusServiceApi;
import com.shuzhi.mapper.DeviceLoopMapper;
import com.shuzhi.mapper.StationMapper;
import com.shuzhi.mapper.TFrtMapper;
import com.shuzhi.mapper.TLightPoleMapper;
import com.shuzhi.rabbitmq.Message;
import com.shuzhi.service.DeviceLoopService;
import com.shuzhi.service.DeviceStationService;
import com.shuzhi.service.MqMessageService;
import com.shuzhi.service.StationService;
import com.shuzhi.websocket.socketvo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zgk
 * @description websocket
 * @date 2019-07-07 16:13
 */
@Slf4j
@ServerEndpoint("/websocket")
@Component
public class WebSocketServer {
    ObjectMapper objectMapper = new ObjectMapper();
    private static StringRedisTemplate redisTemplate;

    private MqMessageService mqMessageService;

    private TStatusService tStatusService;

    private LoopStatusServiceApi loopStatusServiceApi;

    private IotLcdsStatusService iotLcdStatusService;

    private DeviceLoopService deviceLoopService;

    private DeviceStationService deviceStationService;

    private StationService stationService;

    private DeviceLoopMapper deviceLoopMapper;

    private TLightPoleMapper tLightPoleMapper;

    private TEventLedService tEventLedService;

    private TEventLcdService tEventLcdService;

    private StationMapper stationMapper;

    private TFrtMapper tFrtMapper;

    private static Map<String, CopyOnWriteArrayList<Session>> SESSION_MAP = new ConcurrentHashMap<>();
    private static Hashtable<String, Integer> sessionCodeMap = new Hashtable<>();

    private DataClientService dataClientService;

    private DeviceClientService deviceClientService;

    /**
     * lcd设备状态
     */
    private List<IotLcdStatusTwo> allStatusByRedis;

    /**
     * led设备状态
     */
    private List<TStatusDto> allStatus;

    /**
     * 照明设备状态
     */
    private List<TLoopStateDto> loopStatus;

    /**
     * 环测设备状态
     */
    private List<TDataDto> dataAllStatus;
    /**
     * 广播状态
     */

    /**
     * 重试次数
     */
    @Value("${send.retry}")
    private Integer retry;

    /**
     * 当前重试次数
     */
    private int count = 0;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {

    }

    /**
     * 接收消息的方法
     *
     * @param message 收到的消息
     * @param session session
     */
    @OnMessage
    public void onMessage(String message, Session session) throws Exception {

        //获取模块id
        Message message1 = JSON.parseObject(message, Message.class);
        String token = String.valueOf(message1.getModulecode());
        CopyOnWriteArrayList<Session> sessions = SESSION_MAP.get(token);
        //将Modulecode作为key session作为值 一个Modulecode可能会有多个连接 所以session是一个集合
        if (sessions == null) {
            sessions = new CopyOnWriteArrayList<>();
        }
        //同个sessionId和modulecode生成一个唯一标识
        sessions.add(session);
        SESSION_MAP.put(token, sessions);
        AtomicInteger size = new AtomicInteger();
        SESSION_MAP.forEach((s, sessions1) -> size.set(sessions1.size() + size.get()));
        log.info("当前的session数量 : {}", size);
        //保存主机标识
        sessionCodeMap.remove(session.getId());
        sessionCodeMap.put(session.getId(), message1.getModulecode());
        //判断消息类型
        switch (message1.getMsgtype()) {
            //请求
            case "2":
                restHandle(message1);
                break;
            //回执
            case "3":
                receiptHandle(message1);
                break;
            default:
                throw new Exception("消息类型错误");
        }
    }

    /**
     * 处理回执消息
     *
     * @param message 消息
     */
    private void receiptHandle(Message message) {
        //

    }

    /**
     * 处理响应消息
     *
     * @param message 消息
     */
    private void restHandle(Message message) throws ParseException, JsonProcessingException {

        Integer modulecode = message.getModulecode();
        //判断消息编码
        switch (message.getMsgcode()) {
            case 200001:
                break;
            //首次建立连接
            case 200000:
                //回执
                ReceiptHandleVo receiptHandleVo = new ReceiptHandleVo(message.getMsgid(), modulecode, new Date());
                send(String.valueOf(modulecode), JSON.toJSONString(receiptHandleVo));
                //拼装消息
                assemble(message);
            default:
        }
    }

    /**
     * 拼装并推送消息
     *
     * @param message 前端协议
     */
    private void assemble(Message message) throws ParseException, JsonProcessingException {
        //判断是什么设备
        Optional.ofNullable(mqMessageService).orElseGet(() -> mqMessageService = ApplicationContextUtils.get(MqMessageService.class));
        MqMessage mqMessageSelect = new MqMessage();
        mqMessageSelect.setModulecode(message.getModulecode());
        MqMessage mqMessage = mqMessageService.selectOne(mqMessageSelect);
        if (mqMessage != null) {
            switch (mqMessage.getExchange()) {
                case "lcd":
                    //调用lcd设备的信息
                    lcd();
                    break;
                case "led":
                    //调用led设备信息
                    led();
                    break;
                case "light":
                    //调用照明设备信息
                    light();
                    break;
                case "platform":
                    //调用站台信息
                    platform();
                    break;
                case "frt":
                    //调用环测
                    frt();
                    break;
                case "spon":
                    //广播
                    spon();
                    break;
                //用电管理
                case "electricity":
                    //调用站台信息
                    electricity();
                    break;
                default:
            }
        }
    }

    /**
     * 拼装并发送用电管理信息
     */
    @Scheduled(cron = "${send.electricity-cron}")
    private void electricity() {
        //查出led的 moduleCode
        Integer modulecode = getModuleCode("electricity");
        String code = String.valueOf(modulecode);
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            messageVo.setMsgcode(206001);

        }
    }

    /**
     * 拼装并发送站台管理信息
     */
    @Scheduled(cron = "${send.platform-cron}")
    private void platform() throws ParseException {
        //查出led的 moduleCode
        Integer modulecode = getModuleCode("platform");
        String code = String.valueOf(modulecode);
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            messageVo.setMsgcode(202002);
            List<DevicesMsg> lights = new ArrayList<>();
            //判断是什么设备
            Optional.ofNullable(deviceStationService).orElseGet(() -> deviceStationService = ApplicationContextUtils.get(DeviceStationService.class));
            Optional.ofNullable(stationService).orElseGet(() -> stationService = ApplicationContextUtils.get(StationService.class));
            //查询所有的公交站
            Optional.ofNullable(stationMapper).orElseGet(() -> stationMapper = ApplicationContextUtils.get(StationMapper.class));
            List<Station> stations = stationMapper.selectAll();

            DeviceStation deviceStationSelcet = new DeviceStation();
            stations.forEach(station -> {

                Integer stationid = station.getId();
                //查出该公交站下所有的设备
                deviceStationSelcet.setStationid(stationid);
                DevicesMsg devicesMsg = new DevicesMsg();
                devicesMsg.setStationid(stationid);
                devicesMsg.setStationname(station.getStationName());
                List<Devices> devices = new ArrayList<>();

                //添加lcd设备
                try {
                    setLcdDevices(devicesMsg, devices, stationid);
                } catch (Exception e) {
                    log.error("站台管理 获取lcd设备信息失败 : {}", e.getMessage());
                }
                //添加led设备
                try {
                    setLedDevices(devicesMsg, devices, stationid);
                } catch (Exception e) {
                    log.error("站台管理 获取led设备信息失败 : {}", e.getMessage());
                }
                //添加照明设备
                try {
                    setLightDevices(devicesMsg, devices, stationid);
                } catch (Exception e) {
                    log.error("站台管理 获取照明设备信息失败 : {}", e.getMessage());
                }
                lights.add(devicesMsg);
            });
            messageVo.setMsg(lights);
            send(code, JSON.toJSONString(messageVo));
            //拼装并发送站台统计信息
            messageVo.setMsgcode(202001);
            messageVo.setMsg(platformStatis());
            send(code, JSON.toJSONString(messageVo));
            //拼装并发送单个站台的信息
            messageVo.setMsgcode(202003);
            messageVo.setMsg(getSums(lights));
            send(code, JSON.toJSONString(messageVo));
            //发送单站离线设备信息
            messageVo.setMsgcode(202005);
            messageVo.setMsg(new StationsMsg(lights));
            send(code, JSON.toJSONString(messageVo));

            log.info("站台定时任务时间 : {}", messageVo.getTimestamp());
        }
    }

    /**
     * 计算站台设备能耗
     *
     * @param lights 站台设备信息
     * @return 计算结果
     */
    private SumsMsg getSums(List<DevicesMsg> lights) {

        Optional.ofNullable(deviceLoopMapper).orElseGet(() -> deviceLoopMapper = ApplicationContextUtils.get(DeviceLoopMapper.class));
        Optional.ofNullable(stationMapper).orElseGet(() -> stationMapper = ApplicationContextUtils.get(StationMapper.class));

        List<SumsVo> sumsVos = new ArrayList<>();
        List<SumsVo> sumsVoList = new ArrayList<>();

        lights.forEach(devicesMsg -> {
            SumsVo sumsVo = new SumsVo();
            sumsVo.setStationid(devicesMsg.getStationid());
            sumsVo.setStationname(devicesMsg.getStationname());
            //查出该站下所有的设备
            List<DeviceLoop> deviceLoopList = deviceLoopMapper.findByStationId(devicesMsg.getStationid());
            if (deviceLoopList != null && deviceLoopList.size() != 0) {
                //计算能耗
                try {
                    stationConsumption(sumsVo, deviceLoopList);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            sumsVos.add(sumsVo);
        });
        //遍历所有公交站 将公交站名称相同的 加起来
        List<Station> stations = stationMapper.selectAll();
        stations.forEach(station -> {
            SumsVo sumsVo = new SumsVo();
            sumsVo.setStationname(station.getStationName());
            sumsVo.setStationid(station.getId());
            sumsVos.forEach(sumsVo1 -> {
                if (station.getId().equals(sumsVo1.getStationid())) {
                    sumsVo.setData(sumsVo1);
                }
            });
            sumsVoList.add(sumsVo);
        });

        return new SumsMsg(sumsVoList);
    }

    /**
     * 计算公交站的能耗
     *
     * @param sumsVo         要封装的能耗信息
     * @param deviceLoopList deviceLoopList 公交站下的设备信息
     */
    private void stationConsumption(SumsVo sumsVo, List<DeviceLoop> deviceLoopList) throws ParseException {
        //本月能耗
        float currentmonth = 0;
        //上月能耗
        float lastmonth = 0;
        //本年能耗
        float thisyear = 0;
        //灯箱能耗
        float lamphouse = 0;
        //顶棚能耗
        float platfond = 0;
        //logo能耗
        float logo = 0;
        //led能耗
        float led = 0;
        //lcd能耗
        float lcd = 0;

        StatisticsVo statisticsVo = new StatisticsVo();
        for (DeviceLoop deviceLoop : deviceLoopList) {
            //计算能耗
            statisticsVo.setDid(deviceLoop.getGatewayDid());
            statisticsVo.setLoop(deviceLoop.getLoop());

            StatisticsMsgVo statistics = Statistics.findStatistics(statisticsVo);
            currentmonth = currentmonth + statistics.getCurrentmonth();
            lastmonth = lastmonth + statistics.getLastmonth();
            thisyear = thisyear + statistics.getThisyear();

            switch (deviceLoop.getTypecode()) {
                //顶棚照明
                case "1":
                    platfond = platfond + statistics.getActivepowerNow();
                    break;
                //灯箱照明
                case "2":
                    lamphouse = lamphouse + statistics.getActivepowerNow();
                    break;
                //logo
                case "3":
                    logo = logo + statistics.getActivepowerNow();
                    break;
                //led
                case "4":
                    led = led + statistics.getActivepowerNow();
                    break;
                //lcd
                case "5":
                    lcd = lcd + statistics.getActivepowerNow();
                    break;

                default:
            }

        }
        sumsVo.setCurrentmonth(currentmonth);
        sumsVo.setLastmonth(lastmonth);
        sumsVo.setThisyear(thisyear);
        sumsVo.setLamphouse(lamphouse);
        sumsVo.setPlatfond(platfond);
        sumsVo.setLogo(logo);
        sumsVo.setLed(led);
        sumsVo.setLed(led);
    }

    /**
     * 拼装站台统计信息
     *
     * @return 拼装好的消息
     */
    @SuppressWarnings("Duplicates")
    private PlatformStatisVo platformStatis() throws ParseException {

        PlatformStatisVo platformStatisVo = new PlatformStatisVo();
        Optional.ofNullable(deviceLoopService).orElseGet(() -> deviceLoopService = ApplicationContextUtils.get(DeviceLoopService.class));
        DeviceLoop deviceLoopSelect = new DeviceLoop();
        StatisticsVo statisticsVo = new StatisticsVo();
        //本月
        float currentmonth = 0;
        //上月
        float lastmonth = 0;
        //本年
        float thisyear = 0;

        //lcd设备
        if (allStatusByRedis != null) {
            platformStatisVo.setLcdtotal(allStatusByRedis.size());
            //获取开启设备和关闭设备的总数
            platformStatisVo.setLcdonline((int) allStatusByRedis.stream().filter(iotLcdStatusTwo -> "1".equals(iotLcdStatusTwo.getStatus())).count());
            platformStatisVo.setLcdoffline((int) allStatusByRedis.stream().filter(iotLcdStatusTwo -> "0".equals(iotLcdStatusTwo.getStatus())).count());
            //能耗信息
            //查出设备的回路号
            for (IotLcdStatusTwo iotLcdStatusTwo : allStatusByRedis) {
                deviceLoopSelect.setDeviceDid(iotLcdStatusTwo.getId());
                //获得lcd设备的统计信息
                deviceLoopSelect.setDeviceDid(iotLcdStatusTwo.getId());
                deviceLoopSelect.setTypecode("5");
                DeviceLoop deviceLoop = deviceLoopService.selectOne(deviceLoopSelect);
                if (deviceLoop != null) {
                    statisticsVo.setLoop(deviceLoop.getLoop());
                    statisticsVo.setDid(String.valueOf(deviceLoop.getGatewayDid()));
                }
                StatisticsMsgVo statistics = Statistics.findStatistics(statisticsVo);
                currentmonth = currentmonth + statistics.getCurrentmonth();
                lastmonth = lastmonth + statistics.getLastmonth();
                thisyear = thisyear + statistics.getThisyear();
            }
        }
        //获得led设备统计信息
        if (allStatus != null) {
            platformStatisVo.setLedtotal(allStatus.size());
            platformStatisVo.setLedtotal((int) allStatus.stream().filter(tStatusDto -> 1 == (tStatusDto.getState())).count());
            platformStatisVo.setLedtotal((int) allStatus.stream().filter(tStatusDto -> 0 == (tStatusDto.getState())).count());
            //能耗信息
            //查出设备的回路号
            for (TStatusDto status : allStatus) {
                deviceLoopSelect.setDeviceDid(status.getId());
                deviceLoopSelect.setTypecode("4");
                DeviceLoop deviceLoop = deviceLoopService.selectOne(deviceLoopSelect);
                if (deviceLoop != null) {
                    statisticsVo.setLoop(deviceLoop.getLoop());
                    statisticsVo.setDid(String.valueOf(deviceLoop.getGatewayDid()));
                }
                StatisticsMsgVo statistics = Statistics.findStatistics(statisticsVo);
                currentmonth = currentmonth + statistics.getCurrentmonth();
                lastmonth = lastmonth + statistics.getLastmonth();
                thisyear = thisyear + statistics.getThisyear();
            }
        }
        //获得照明设备统计信息
        if (loopStatus != null) {
            platformStatisVo.setLighttotal(loopStatus.size());
            platformStatisVo.setLightonline((int) loopStatus.stream().filter(loopStateDto -> 1 == (loopStateDto.getState())).count());
            platformStatisVo.setLightoffline((int) loopStatus.stream().filter(loopStateDto -> 0 == (loopStateDto.getState())).count());
            //能耗信息
            //查出设备的回路号
            for (TLoopStateDto status : loopStatus) {
                statisticsVo.setLoop(status.getLoop());
                statisticsVo.setDid(String.valueOf(status.getGatewayId()));
                StatisticsMsgVo statistics = Statistics.findStatistics(statisticsVo);
                currentmonth = currentmonth + statistics.getCurrentmonth();
                lastmonth = lastmonth + statistics.getLastmonth();
                thisyear = thisyear + statistics.getThisyear();
            }
        }
        platformStatisVo.setCurrentmonth(currentmonth);
        platformStatisVo.setLastmonth(lastmonth);
        platformStatisVo.setThisyear(thisyear);
        return platformStatisVo;
    }

    /**
     * 封装照明设备信息
     *
     * @param devicesMsg 总设备信息
     * @param devices    照明设备信息
     * @param stationid  站台id
     */
    private void setLightDevices(DevicesMsg devicesMsg, List<Devices> devices, Integer stationid) {

        //判断照明设备是否为空
        Optional.ofNullable(loopStatus).orElseGet(() -> {
            Optional.ofNullable(loopStatusServiceApi).orElseGet(() -> loopStatusServiceApi = ApplicationContextUtils.get(LoopStatusServiceApi.class));
            loopStatus = loopStatusServiceApi.findLoopStatus();
            return loopStatus;
        });
        Optional.ofNullable(deviceLoopService).orElseGet(() -> deviceLoopService = ApplicationContextUtils.get(DeviceLoopService.class));
        Optional.ofNullable(deviceStationService).orElseGet(() -> deviceStationService = ApplicationContextUtils.get(DeviceStationService.class));
        //判断该设备是否在该网关下
        DeviceStation deviceStationSelect = new DeviceStation();
        loopStatus.forEach(loopStateDto -> {
        //通过回路id查出设备id
        DeviceLoop deviceLoopSelect = new DeviceLoop();
        deviceLoopSelect.setGatewayDid(loopStateDto.getGatewayId());
        deviceLoopSelect.setLoop(loopStateDto.getLoop());
        DeviceLoop deviceLoop = deviceLoopService.selectOne(deviceLoopSelect);
        if (deviceLoop != null) {
            deviceStationSelect.setDeviceDid(deviceLoop.getDeviceDid());
            deviceStationSelect.setTypecode(deviceLoop.getTypecode());
            DeviceStation deviceStation = deviceStationService.selectOne(deviceStationSelect);
            if (deviceStation != null) {
                    if (stationid.equals(deviceStation.getStationid())) {
                        Devices device = new Devices(loopStateDto);
                        devices.add(device);
                    }
                }
            }
        });
        devicesMsg.setDevices(devices);
    }

    /**
     * 站台管理 添加led设备
     *
     * @param devicesMsg 要返回的设备信息
     * @param devices    所有设备信息
     * @param stationid  站台id
     */
    private void setLedDevices(DevicesMsg devicesMsg, List<Devices> devices, Integer stationid) {

        //判断led设备是否为空
        Optional.ofNullable(allStatus).orElseGet(() -> {
            Optional.ofNullable(tStatusService).orElseGet(() -> tStatusService = ApplicationContextUtils.get(TStatusService.class));
            allStatus = tStatusService.findAllStatusByRedis();
            return allStatus;
        });
        //判断该设备是否在该网关下
        isInGateway(devicesMsg, devices, stationid, "4");

    }


    /**
     * 站台管理添加lcd设备
     *
     * @param devicesMsg 要返回的设备信息
     * @param devices    所有设备信息
     * @param stationid  站台id
     */
    private void setLcdDevices(DevicesMsg devicesMsg, List<Devices> devices, Integer stationid) {

        Optional.ofNullable(deviceStationService).orElseGet(() -> deviceStationService = ApplicationContextUtils.get(DeviceStationService.class));
        Optional.ofNullable(iotLcdStatusService).orElseGet(() -> iotLcdStatusService = ApplicationContextUtils.get(IotLcdsStatusService.class));
        //判断lcd设备是否为空 如果为空就去查询
        Optional.ofNullable(allStatusByRedis).orElseGet(() -> {
            Optional.ofNullable(loopStatusServiceApi).orElseGet(() -> loopStatusServiceApi = ApplicationContextUtils.get(LoopStatusServiceApi.class));
            allStatusByRedis = iotLcdStatusService.findAllStatusByRedis();
            return allStatusByRedis;
        });
        //判断该设备是否在该站台下
        isInGateway(devicesMsg, devices, stationid, "5");
    }

    /**
     * 提取重复代码 判断该设备是否在该网关下
     *
     * @param devicesMsg 要封装的设备
     * @param devices    设备
     * @param stationid  公交站id
     * @param s
     */
    @SuppressWarnings("Duplicates")
    private void isInGateway(DevicesMsg devicesMsg, List<Devices> devices, Integer stationid, String s) {
        DeviceStation deviceStationSelect = new DeviceStation();
        if (StringUtils.equals("4", s)) {
            allStatus.stream().filter(iotLedStatus -> {
                //查出设备信息
                deviceStationSelect.setTypecode(s);
                deviceStationSelect.setDeviceDid(iotLedStatus.getId());
                DeviceStation deviceStation = deviceStationService.selectOne(deviceStationSelect);
                //判断是否在该公交站下
                if (deviceStation != null) {
                    return stationid.equals(deviceStation.getStationid());
                }
                return false;
            })
                    .forEach(iotLcdStatus -> {
                        Devices device = new Devices(iotLcdStatus);
                        devices.add(device);
                    });
        } else {
            allStatusByRedis.stream().filter(iotLedStatus -> {
                deviceStationSelect.setTypecode(s);
                deviceStationSelect.setDeviceDid(iotLedStatus.getId());
                DeviceStation deviceStation = deviceStationService.selectOne(deviceStationSelect);
                if (deviceStation != null) {
                    return stationid.equals(deviceStation.getStationid());
                }
                return false;
            })
                    .forEach(iotLcdStatus -> {
                        Devices device = new Devices(iotLcdStatus);
                        devices.add(device);
                    });
        }
        devicesMsg.setDevices(devices);
    }


    /**
     * 照明首次连接 同时也定时推送
     */
    @Scheduled(cron = "${send.light-cron}")
    public void light() throws ParseException, JsonProcessingException {
        Integer modulecode = getModuleCode("light-frt");
        String code = String.valueOf(modulecode);
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            messageVo.setMsgcode(220001);
            //调用接口 获取当前照明状态
            Optional.ofNullable(loopStatusServiceApi).orElseGet(() -> loopStatusServiceApi = ApplicationContextUtils.get(LoopStatusServiceApi.class));
            Optional.ofNullable(deviceLoopMapper).orElseGet(() -> deviceLoopMapper = ApplicationContextUtils.get(DeviceLoopMapper.class));
            Optional.ofNullable(tLightPoleMapper).orElseGet(() -> tLightPoleMapper = ApplicationContextUtils.get(TLightPoleMapper.class));
            Optional.ofNullable(tFrtMapper).orElseGet(() -> tFrtMapper = ApplicationContextUtils.get(TFrtMapper.class));
            loopStatus = loopStatusServiceApi.findLoopStatus();
            List<Loops> loops = new ArrayList<>();
            List<Integer> count = new ArrayList<>();
            if (loopStatus != null && loopStatus.size() != 0) {
                //所有单灯信息
                loopStatus.forEach(tLoopStateDto -> {
                    Loops loops1 = new Loops();
                    //根据灯杆id查询
                    TLoopStateDto tLoopStateDto1 = new TLoopStateDto();
                    tLoopStateDto1.setGatewayId(tLoopStateDto.getGatewayId());
                    tLoopStateDto1.setLoop(tLoopStateDto.getLoop());
                    //根据设备did查找灯杆id
                    Integer strings = deviceLoopMapper.findsByLamppostId(tLoopStateDto1);
                    if(!count.contains(strings)){
                        List<Lights> light = new ArrayList<>();
                        loops1.setLoopid(tLoopStateDto.getId());
                        loops1.setLoopnum(tLoopStateDto.getLoop());
                        loops1.setLoopname(tLoopStateDto.getName());
                        loops1.setOnoff(tLoopStateDto.getState());
                        loops1.setState(tLoopStateDto.getState());
                        loops1.setLights(light);
                        count.add(strings);
                        DeviceLoop deviceLoop = new DeviceLoop();
                        deviceLoop.setLamppostid(strings);
                        List<DeviceLoop> deviceLoops = deviceLoopMapper.select(deviceLoop);
                        for (DeviceLoop loop : deviceLoops) {
                            Lights lights = new Lights();
                            //在根据灯杆id查询所有灯杆信息
                            TLightPole lists = tLightPoleMapper.findByTlightPoles(loop.getLamppostid());
                            lights.setLamppostid(lists.getLamppostid());
                            lights.setLamppostname(lists.getLamppostname());
                            deviceLoop.setLamppostid(null);
                            deviceLoop.setLoop(loop.getLoop());
                            deviceLoop.setGatewayDid(loop.getGatewayDid());
                            DeviceLoop deviceLoop1 = deviceLoopMapper.selectOne(deviceLoop);
                            lights.setName(deviceLoop1.getDeviceName());
                            lights.setState(tLoopStateDto.getState());
                            lights.setOnoff(tLoopStateDto.getState());
                            light.add(lights);
                        }
                        loops1.setLights(light);
                        loops.add(loops1);
                    }
                });
                LightMsg lightMsg = new LightMsg(loops);
                messageVo.setMsg(lightMsg);
                send(code, JSON.toJSONString(messageVo, SerializerFeature.DisableCircularReferenceDetect));
                //所有单灯信息（回路）
               // send(code,objectMapper.writeValueAsString(messageVo));

                //所有单灯信息（分组）
                List<Groups> groups = new ArrayList<>();
                List<Integer> count1 = new ArrayList<>();
                if (loopStatus != null && loopStatus.size() != 0) {
                    loopStatus.forEach(tLoopStateDto -> {

                        Groups groups1 = new Groups();
                        //根据灯杆id查询
                        TLoopStateDto tLoopStateDto1 = new TLoopStateDto();
                        tLoopStateDto1.setGatewayId(tLoopStateDto.getGatewayId());
                        tLoopStateDto1.setLoop(tLoopStateDto.getLoop());
                        //根据设备did查找灯杆id
                        Integer strings = deviceLoopMapper.findsByLamppostId(tLoopStateDto1);
                        if(!count1.contains(strings)){
                            List<Lights> light1 = new ArrayList<>();
                            //根据灯杆id查询
                            TLightPole lists = tLightPoleMapper.findByTlightPoles(strings);
                            //根据灯杆中的frtid查询
                            Group groups2 = tFrtMapper.findById(lists.getFrtid());
                            groups1.setGroupid(groups2.getId());
                            groups1.setGroupname(groups2.getFrtName());
                            count1.add(strings);

                            DeviceLoop deviceLoop = new DeviceLoop();
                            deviceLoop.setLamppostid(strings);
                            List<DeviceLoop> deviceLoops = deviceLoopMapper.select(deviceLoop);
                            for (DeviceLoop loop : deviceLoops) {
                                Lights lights1 = new Lights();
                                //在根据灯杆id查询所有灯杆信息
                                TLightPole lists2 = tLightPoleMapper.findByTlightPoles(loop.getLamppostid());
                                lights1.setLamppostid(lists2.getLamppostid());
                                lights1.setLamppostname(lists2.getLamppostname());
                                deviceLoop.setLamppostid(null);
                                deviceLoop.setLoop(loop.getLoop());
                                deviceLoop.setGatewayDid(loop.getGatewayDid());
                                DeviceLoop deviceLoop1 = deviceLoopMapper.selectOne(deviceLoop);
                                lights1.setName(deviceLoop1.getDeviceName());
                                lights1.setState(tLoopStateDto.getState());
                                lights1.setOnoff(tLoopStateDto.getState());
                                light1.add(lights1);
                            }
                            groups1.setLights(light1);
                            groups.add(groups1);
                        }
                    });
                }
                LightsMsg lightsMsg = new LightsMsg(groups);
                messageVo.setMsg(lightsMsg);
                messageVo.setMsgcode(220002);
                send(code,objectMapper.writeValueAsString(messageVo));

                //推送统计信息
                StatisticsMsgsVo statisticsMsgsVo = new StatisticsMsgsVo();
                statisticsMsgsVo.addLightNum(loopStatus);
                messageVo.setMsg(statisticsMsgsVo);
                messageVo.setMsgcode(220003);
                send(code,objectMapper.writeValueAsString(messageVo));

                //推送离线设备信息
                messageVo.setMsgcode(220004);
                OfflineMsg offlineMsg = new OfflineMsg();
                offlineMsg.offlineLightMsg(loopStatus);
                messageVo.setMsg(offlineMsg);
                send(code,objectMapper.writeValueAsString(messageVo));

                //所有集中控制器信息
                List<Integer> count2 = new ArrayList<>();
                List<Controllers> controllersList = new ArrayList<>();
                loopStatus.forEach(tLoopStateDto -> {
                    DeviceLoop deviceLoop = new DeviceLoop();
                    deviceLoop.setGatewayDid(tLoopStateDto.getGatewayId());
                    deviceLoop.setTypecode("7");
                    List<DeviceLoop> select = deviceLoopMapper.select(deviceLoop);
                    Set<DeviceLoop> deviceLoops = new HashSet<>(select);
                    for (DeviceLoop loop : deviceLoops) {
                        List<Loops> loops1 = new ArrayList<>();
                        Controllers controllers = new Controllers();
                        controllers.setControllerid(loop.getGatewayDid());
                        controllers.setControllername(loop.getDeviceName());
                        controllers.setControllernum(loop.getGatewayDid());
                        List<GatewayRedis> gatewayRedis = loopStatusServiceApi.findgatewayState();
                        for (GatewayRedis gatewayRedi : gatewayRedis) {
                            if (loop.getGatewayDid().equals(gatewayRedi.getGatewayId())){
                                controllers.setState(gatewayRedi.getOnoff());
                            } else {
                                controllers.setState(0);
                            }
                        }
                        //根据灯杆id查询
                        TLoopStateDto tLoopStateDto2 = new TLoopStateDto();
                        tLoopStateDto2.setGatewayId(tLoopStateDto.getGatewayId());
                        tLoopStateDto2.setLoop(tLoopStateDto.getLoop());
                        //根据设备did查找灯杆id
                        Integer strings2 = deviceLoopMapper.findsByLamppostId(tLoopStateDto2);
                        if(!count2.contains(strings2)) {
                            count2.add(strings2);
                            Loops loops2 = new Loops();
                            loops2.setLoopid(tLoopStateDto.getId());
                            loops2.setLoopnum(tLoopStateDto.getLoop());
                            loops2.setLoopname(tLoopStateDto.getName());
                            loops2.setOnoff(tLoopStateDto.getState());
                            loops1.add(loops2);
                        }
                        controllers.setLoopsList(loops1);
                        controllersList.add(controllers);
                    }

                });
                messageVo.setMsgcode(220005);
                messageVo.setMsg(controllersList);
                send(code,objectMapper.writeValueAsString(messageVo));

                log.info("照明定时任务时间 : {}", messageVo.getTimestamp());
            }
        }
    }

    /**
     * 照明设备统计信息
     *
     * @param loopStatus 照明设备信息
     * @return 统计信息
     */
    private StatisticsMsgVo lightStatis(List<TLoopStateDto> loopStatus) throws ParseException {

        List<String> dids1 = new ArrayList<>();
        List<String> dids2 = new ArrayList<>();
        List<String> dids3 = new ArrayList<>();
        //取出所有的did
        loopStatus.forEach(loopStateDto -> {
            //通过回路号查询这个是什么设备
            DeviceLoopService deviceLoopService = ApplicationContextUtils.get(DeviceLoopService.class);
            DeviceLoop deviceLoopSelect = new DeviceLoop();
            deviceLoopSelect.setLoop(loopStateDto.getLoop());
            deviceLoopSelect.setGatewayDid(loopStateDto.getGatewayId());
            DeviceLoop deviceLoop = deviceLoopService.selectOne(deviceLoopSelect);
            if (deviceLoop != null) {
                if ("1".equals(deviceLoop.getTypecode())) {
                    dids1.add(String.valueOf(deviceLoop.getDeviceDid()));
                }
                if ("1".equals(deviceLoop.getTypecode())) {
                    dids2.add(String.valueOf(deviceLoop.getDeviceDid()));
                }
                if ("1".equals(deviceLoop.getTypecode())) {
                    dids3.add(String.valueOf(deviceLoop.getDeviceDid()));
                }
            }
        });
        //统计
        StatisticsMsgVo statisticsMsgVo1 = equipStatis(dids1, "1");
        StatisticsMsgVo statisticsMsgVo2 = equipStatis(dids1, "2");
        StatisticsMsgVo statisticsMsgVo3 = equipStatis(dids1, "3");

        return new StatisticsMsgVo(statisticsMsgVo1, statisticsMsgVo2, statisticsMsgVo3);
    }


    /**
     * lcd首次连接信息 也需要定时向前台推送
     */
    @Scheduled(cron = "${send.lcd-cron}")
    public void lcd() throws ParseException {
        //查出led的 moduleCode
        Integer modulecode = getModuleCode("lcd-frt");
        String code = String.valueOf(modulecode);
        //判断该连接是要被关闭
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            //调用接口 获得所有站屏的设备状态
            messageVo.setMsgcode(230001);
            Optional.ofNullable(tEventLcdService).orElseGet(() -> tEventLcdService = ApplicationContextUtils.get(TEventLcdService.class));
            Optional.ofNullable(iotLcdStatusService).orElseGet(() -> iotLcdStatusService = ApplicationContextUtils.get(IotLcdsStatusService.class));
            Optional.ofNullable(deviceLoopMapper).orElseGet(() -> deviceLoopMapper = ApplicationContextUtils.get(DeviceLoopMapper.class));
            Optional.ofNullable(tLightPoleMapper).orElseGet(() -> tLightPoleMapper = ApplicationContextUtils.get(TLightPoleMapper.class));
            this.allStatusByRedis = iotLcdStatusService.findAllStatusByRedis();
            if (allStatusByRedis != null && allStatusByRedis.size() != 0) {
                List<Lcdss> listLcd = new ArrayList<>();
                LcdMsgs lcdMsg = new LcdMsgs();
                for (IotLcdStatusTwo allStatusByRedi : allStatusByRedis) {
                    Lcdss lcdss = new Lcdss();
                    //根据设备did查找灯杆id
                    List<Integer> strings = deviceLoopMapper.findByLamppostId(allStatusByRedi.getId(),allStatusByRedi.getName());
                    //在根据灯杆id查询所有灯杆信息
                    List<TLightPole> lists = tLightPoleMapper.findByTlightPole(strings);
                    for (TLightPole list : lists) {
                        lcdss.setLamppostid(list.getLamppostid());
                        lcdss.setLamppostname(list.getLamppostname());
                    }
                    try {
                        LcdConversionUtil.fatherToChild(allStatusByRedi,lcdss);
                        listLcd.add(lcdss);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                lcdMsg.setLcds(listLcd);
                messageVo.setMsg(lcdMsg);
                send(code, JSON.toJSONString(messageVo));

                //推送设备状态 将多余字段设置为 null
                allStatusByRedis.forEach(iotLcdStatus -> iotLcdStatus.setVolume(null));
                LcdMsg lcdMsg2 = new LcdMsg(allStatusByRedis);
                messageVo.setMsg(lcdMsg2);
                messageVo.setMsgcode(230002);
                send(code, JSON.toJSONString(messageVo));

                //推送统计信息
                LightMsgVo lightMsgVo = new LightMsgVo();
                lightMsgVo.lightMsgVoLcd(allStatusByRedis, tEventLcdService.findCountByTime());
                messageVo.setMsg(lightMsgVo);
                messageVo.setMsgcode(230003);
                send(code, JSON.toJSONString(messageVo));

                //推送离线设备信息
                messageVo.setMsgcode(230004);
                OfflineMsg offlineMsg = new OfflineMsg();
                offlineMsg.offlineLcdMsg(allStatusByRedis);
                messageVo.setMsg(offlineMsg);
                send(code, JSON.toJSONString(messageVo));

                log.info("lcd定时任务时间 : {}", messageVo.getTimestamp());
            }
        }
    }

    /**
     * 判断该连接是否被关闭
     *
     * @param code modulecode
     * @return 判断结果
     */
    private boolean isOnClose(String code) {
        CopyOnWriteArrayList<Session> sessions = SESSION_MAP.get(code);
        if (sessions != null) {
            for (Session session : sessions) {
                if (StringUtils.equals(code, String.valueOf(sessionCodeMap.get(session.getId())))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 重载判断该连接是否被关闭
     *
     * @param code modulecode
     * @return 判断结果
     */
    private synchronized boolean isOnClose(String code, String sessionId) {
        return StringUtils.equals(code, String.valueOf(sessionCodeMap.get(sessionId)));
    }

    /**
     * lcd设备统计信息
     *
     * @param allStatusByRedis 设备did
     * @return 能耗信息
     */
    private StatisticsMsgVo lcdStatis(List<IotLcdStatusTwo> allStatusByRedis) throws ParseException {

        List<String> dids = new ArrayList<>();
        //取出所有的did
        allStatusByRedis.forEach(iotLcdStatus -> dids.add(iotLcdStatus.getId()));
        //统计
        return equipStatis(dids, "5");
    }

    /**
     * led首次连接信息 也需要定时向前台推送
     */
    @Scheduled(cron = "${send.led-cron}")
    public void led() throws ParseException {
        //查出led的 moduleCode
        Integer modulecode = getModuleCode("led-frt");
        String code = String.valueOf(modulecode);
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            messageVo.setMsgcode(230011);
            //调用接口
            Optional.ofNullable(tEventLedService).orElseGet(() -> tEventLedService = ApplicationContextUtils.get(TEventLedService.class));
            Optional.ofNullable(tStatusService).orElseGet(() -> tStatusService = ApplicationContextUtils.get(TStatusService.class));
            Optional.ofNullable(deviceLoopMapper).orElseGet(() -> deviceLoopMapper = ApplicationContextUtils.get(DeviceLoopMapper.class));
            Optional.ofNullable(tLightPoleMapper).orElseGet(() -> tLightPoleMapper = ApplicationContextUtils.get(TLightPoleMapper.class));
            allStatus = tStatusService.findAllStatusByRedis();
            List<Ledss> listLed = new ArrayList<>();
            if (allStatus != null && allStatus.size() != 0) {
                LedMsgs ledMsgs = new LedMsgs();
                for (TStatusDto tStatusDto : allStatus) {
                    Ledss ledss = new Ledss();
                    //根据设备did查找灯杆id
                    List<Integer> strings = deviceLoopMapper.findByLamppostId(tStatusDto.getId(),tStatusDto.getName());
                    if (CollectionUtils.isEmpty(strings)) {
                        ledss.setLamppostid(null);
                        ledss.setLamppostname(null);
                    } else {
                        //在根据灯杆id查询所有灯杆信息
                        List<TLightPole> lists = tLightPoleMapper.findByTlightPole(strings);
                        if (CollectionUtils.isEmpty(lists)) {
                            ledss.setLamppostid(null);
                            ledss.setLamppostname(null);
                        } else {
                            for (TLightPole list : lists) {
                                ledss.setLamppostid(list.getLamppostid());
                                ledss.setLamppostname(list.getLamppostname());
                            }
                        }
                    }
                    try {
                        LcdConversionUtil.fatherToChild(tStatusDto, ledss);
                        listLed.add(ledss);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ledMsgs.setLeds(listLed);
                messageVo.setMsg(ledMsgs);
                send(code, JSON.toJSONString(messageVo));

                allStatus.forEach(tStatusDto -> {
                    tStatusDto.setVolume(null);
                    tStatusDto.setLight(null);
                });
                Leds leds2 = new Leds(allStatus);
                messageVo.setMsg(leds2);
                messageVo.setMsgcode(230012);
                send(code, JSON.toJSONString(messageVo));

                //推送统计信息
                LightMsgVo lightMsgVo = new LightMsgVo();
                lightMsgVo.lightMsgVoLed(allStatus, tEventLedService.findCountByTime());
                messageVo.setMsg(lightMsgVo);
                messageVo.setMsgcode(230013);
                send(code, JSON.toJSONString(messageVo));

                //推送离线设备信息
                messageVo.setMsgcode(230014);
                OfflineMsg offlineMsg = new OfflineMsg();
                offlineMsg.offlineLedMsg(allStatus);
                messageVo.setMsg(offlineMsg);
                send(code, JSON.toJSONString(messageVo));

                log.info("led定时任务时间 : {}", messageVo.getTimestamp());
            }
        }
    }

    /**
     * led设备统计信息
     *
     * @param allStatus led信息
     * @return 统计信息
     */
    private StatisticsMsgVo ledStatis(List<TStatusDto> allStatus) throws ParseException {

        List<String> dids = new ArrayList<>();
        //取出所有的did
        allStatus.forEach(tStatusDto -> dids.add(tStatusDto.getDid()));
        //统计
        StatisticsMsgVo statisticsMsgVo = equipStatis(dids, "4");
        statisticsMsgVo.addNum(allStatus);
        return statisticsMsgVo;

    }
    /**
     * frt环测首次建连
     */
    @Scheduled(cron = "${send.frt-cron}")
    private void frt() throws ParseException {
        //查出frt的 moduleCode
        Integer modulecode = getModuleCode("ring-frt");
        String code = String.valueOf(modulecode);
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            messageVo.setMsgcode(240001);
            //调用接口
            Optional.ofNullable(dataClientService).orElseGet(() -> dataClientService = ApplicationContextUtils.get(DataClientService.class));
            Optional.ofNullable(deviceLoopMapper).orElseGet(() -> deviceLoopMapper = ApplicationContextUtils.get(DeviceLoopMapper.class));
            Optional.ofNullable(tLightPoleMapper).orElseGet(() -> tLightPoleMapper = ApplicationContextUtils.get(TLightPoleMapper.class));
            Optional.ofNullable(deviceClientService).orElseGet(() -> deviceClientService = ApplicationContextUtils.get(DeviceClientService.class));
            dataAllStatus = dataClientService.findAllStatus();
            if (dataAllStatus != null && dataAllStatus.size() != 0) {
                for (TDataDto status : dataAllStatus) {
                    //根据设备did查找灯杆id
                    List<Integer> strings = deviceLoopMapper.findByLamppostId(status.getDid(),status.getName());
                    //在根据灯杆id查询所有灯杆信息
                    List<TLightPole> lists = tLightPoleMapper.findByTlightPole(strings);
                    for (TLightPole list : lists) {
                        status.setLamppostid(list.getLamppostid());
                        status.setLamppostname(list.getLamppostname());
                    }
                }
                //所有环测的状态信息
                TDataVo tDataVo = new TDataVo(dataAllStatus);
                messageVo.setMsg(tDataVo);
                send(code, JSON.toJSONString(messageVo));
                //推送环测统计
                StatisticalPoleVo number = deviceClientService.number();
                messageVo.setMsg(number);
                messageVo.setMsgcode(240002);
                send(code, JSON.toJSONString(messageVo));

                //离线设备信息
                List<OfflinesRingVo> offline = deviceClientService.offline();
                messageVo.setMsgcode(240003);
                OfflinesRing offlinesRing = new OfflinesRing();
                offlinesRing.setOfflines(offline);
                messageVo.setMsg(offlinesRing);
                send(code, JSON.toJSONString(messageVo));
                log.info("环测定时任务时间 : {}", messageVo.getTimestamp());
            }
        }
    }
    /**
     * spon广播首次建连
     */
    //@Scheduled(cron = "${send.spon-cron}")
    private void spon() throws ParseException {
        //查出spon的 moduleCode
        Integer modulecode = getModuleCode("spon");
        String code = String.valueOf(modulecode);
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            messageVo.setMsgcode(260001);
        }
    }
    /**
     * 关闭连接的方法
     *
     * @param session session
     */
    @OnClose
    public synchronized static void onClose(Session session) {
        log.info("有连接关闭 sessionId : {}", session.getId());
        Map<String, CopyOnWriteArrayList<Session>> map = new HashMap<>(16);
        if (SESSION_MAP != null && SESSION_MAP.size() != 0) {
            SESSION_MAP.forEach((k, v) -> {
                //这里使用迭代器防止角标越界
                if (v != null && v.size() != 0) {
                    v.removeIf(next -> StringUtils.equals(next.getId(), session.getId()));
                    map.put(k, v);
                }
            });
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            SESSION_MAP = map;
            //删除缓存
            Optional.ofNullable(redisTemplate).orElseGet(() -> redisTemplate = ApplicationContextUtils.get(StringRedisTemplate.class));
            sessionCodeMap.remove(session.getId());
            log.info("断开连接后session数量 : {}", SESSION_MAP.size());
        }
    }

    /**
     * 发送消息的方法
     *
     * @param token   modulecode
     * @param message 要发送的信息
     */
    public void send(String token, String message) {

        if (StringUtils.isNotBlank(token)) {
            //遍历session推送消息到页面
            Optional.ofNullable(SESSION_MAP.get(token)).ifPresent(session -> {
                if (session.size() != 0) {
                    session.forEach(session1 -> {
                        try {
                            if (isOnClose(token, session1.getId())) {
                                session1.getBasicRemote().sendText(message);
                                count = 0;
                            }
                        } catch (Exception e) {
                            //重新推送
                            if (2 != count) {
                                send(token, message);
                                log.info("消息推送失败,重试第 {} 次", count + 1);
                                count++;
                            } else {
                                count = 0;
                                onClose(session1);
                                log.error("消息推送失败 : {}", e.getMessage());
                            }
                        }
                    });
                }
            });
            //删除缓存
            Optional.ofNullable(redisTemplate).orElseGet(() -> redisTemplate = ApplicationContextUtils.get(StringRedisTemplate.class));
            try {
                redisTemplate.opsForHash().delete("web_socket_key", JSON.parseObject(message, Message.class).getMsgid());
            } catch (Exception e) {
                log.error("删除缓存错误 : {}", e.getMessage());
            }
        }
    }

    /**
     * 发生错误调用的方法
     *
     * @param session session
     * @param error   错误信息
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误 sessionId : {} ", session.getId());
        //发生错误后 该session会被关闭 要被删除
        //     onClose(session);
        error.printStackTrace();
    }

    /**
     * 将时间格式化为 yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param date 要格式化的时间
     * @return 格式化的结果
     */
    private String dateFormat(Date date) {

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date);
    }

    /**
     * 提取重复代码
     *
     * @param modulecode modulecode
     * @return MessageVo
     */
    private MessageVo setMessageVo(Integer modulecode) {
        //拼装消息
        MessageVo messageVo = new MessageVo();
        messageVo.setMsgid(UUID.randomUUID().toString());
        messageVo.setModulecode(modulecode);
        messageVo.setMsgtype(1);
        messageVo.setTimestamp(dateFormat(new Date()));

        return messageVo;
    }

    /**
     * 提取重复代码 获取Modulecode
     *
     * @return Modulecode
     */
    private Integer getModuleCode(String equipment) {
        Optional.ofNullable(mqMessageService).orElseGet(() -> mqMessageService = ApplicationContextUtils.get(MqMessageService.class));
        MqMessage mqMessageSelect = new MqMessage();
        mqMessageSelect.setExchange(equipment);
        return mqMessageService.selectOne(mqMessageSelect).getModulecode();
    }

    /**
     * 提取重复代码
     *
     * @param dids     设备did
     * @param typeCode 设备类型
     * @return 统计信息
     * @throws ParseException 时间格式化异常
     */
    private StatisticsMsgVo equipStatis(List<String> dids, String typeCode) throws ParseException {

        Optional.ofNullable(deviceLoopService).orElseGet(() -> deviceLoopService = ApplicationContextUtils.get(DeviceLoopService.class));
        //遍历通过did查出回路
        DeviceLoop deviceLoopSelect = new DeviceLoop();
        //本月
        float currentmonth = 0;
        //上月
        float lastmonth = 0;
        //本年
        float thisyear = 0;
        //当前
        float activepowerNow = 0;
        for (String did : dids) {
            if (StringUtils.isNotBlank(did)) {
                deviceLoopSelect.setDeviceDid(did);
                deviceLoopSelect.setTypecode(typeCode);
                DeviceLoop deviceLoop = deviceLoopService.selectOne(deviceLoopSelect);
                //查出单个设备的统计信息
                if (deviceLoop != null) {
                    StatisticsVo statisticsVoSelect = new StatisticsVo();
                    statisticsVoSelect.setDid(String.valueOf(deviceLoop.getGatewayDid()));
                    statisticsVoSelect.setLoop(deviceLoop.getLoop());
                    StatisticsMsgVo statistics = Statistics.findStatistics(statisticsVoSelect);
                    currentmonth = currentmonth + statistics.getCurrentmonth();
                    lastmonth = lastmonth + statistics.getLastmonth();
                    thisyear = thisyear + statistics.getThisyear();
                    activepowerNow = activepowerNow + statistics.getActivepowerNow();
                }
            }
        }
        return new StatisticsMsgVo(currentmonth, lastmonth, thisyear, activepowerNow);
    }
    private DeviceLoop tLoopStateDtoIsNull(TLoopStateDto tLoopStateDto) {
        //通过回路号查询这个是什么设备
        Optional.ofNullable(deviceLoopService).orElseGet(() -> deviceLoopService = ApplicationContextUtils.get(DeviceLoopService.class));
        DeviceLoop deviceLoopSelect = new DeviceLoop();
        deviceLoopSelect.setLoop(tLoopStateDto.getLoop());
        deviceLoopSelect.setGatewayDid(tLoopStateDto.getGatewayId());
        DeviceLoop deviceLoop = deviceLoopService.selectOne(deviceLoopSelect);
        if (deviceLoop != null) {
            if (StringUtils.equals(deviceLoop.getTypecode(), "1") || StringUtils.equals(deviceLoop.getTypecode(), "2") || StringUtils.equals(deviceLoop.getTypecode(), "3")) {
                return deviceLoop;
            }
        }
        return null;
    }

    /**
     * 安公交站整个设备
     *
     * @param lights 设备
     * @return 拼装
     */
    private List<DevicesMsg> lightsStation(List<DevicesMsg> lights) {

        Optional.ofNullable(stationMapper).orElseGet(() -> stationMapper = ApplicationContextUtils.get(StationMapper.class));
        List<DevicesMsg> lightList = new ArrayList<>();
        //遍历所有公交站 将公交站名称相同的 加起来
        List<Station> stations = stationMapper.selectAll();
        stations.forEach(station -> {
            DevicesMsg devicesMsg = new DevicesMsg();
            devicesMsg.setStationname(station.getStationName());
            devicesMsg.setStationid(station.getId());
            lights.forEach(devicesMsg1 -> {
                if (station.getId().equals(devicesMsg1.getStationid())) {
                    devicesMsg.setData(devicesMsg1);
                }
            });
            lightList.add(devicesMsg);
        });
        return lightList;

    }
}
