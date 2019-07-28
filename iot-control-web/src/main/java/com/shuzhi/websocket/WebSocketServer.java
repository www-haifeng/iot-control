package com.shuzhi.websocket;




import com.alibaba.fastjson.JSON;
import com.shuzhi.entity.DeviceLoop;
import com.shuzhi.entity.DeviceStation;
import com.shuzhi.entity.MqMessage;
import com.shuzhi.lcd.entities.IotLcdStatusTwo;
import com.shuzhi.lcd.service.IotLcdsStatusService;
import com.shuzhi.led.entities.TStatusDto;
import com.shuzhi.led.service.TStatusService;
import com.shuzhi.light.entities.StatisticsVo;
import com.shuzhi.light.entities.TLoopStateDto;
import com.shuzhi.light.service.LoopStatusServiceApi;
import com.shuzhi.rabbitmq.Message;
import com.shuzhi.service.DeviceLoopService;
import com.shuzhi.service.DeviceStationService;
import com.shuzhi.service.MqMessageService;
import com.shuzhi.service.StationService;
import com.shuzhi.websocket.socketvo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    private static StringRedisTemplate redisTemplate;

    private MqMessageService mqMessageService;

    private TStatusService tStatusService;

    private LoopStatusServiceApi loopStatusServiceApi;

    private IotLcdsStatusService iotLcdStatusService;

    private DeviceLoopService deviceLoopService;

    private DeviceStationService deviceStationService;

    private StationService stationService;

    private static Map<String, CopyOnWriteArrayList<Session>> SESSION_MAP = new ConcurrentHashMap<>();

    private static Hashtable<String,Integer> sessionCodeMap = new Hashtable<>();

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
     * 连接建立成功调用的方法
     */
    @OnOpen
    public synchronized void onOpen(Session session) {

    }


    /**
     * 接收消息的方法
     *
     * @param message 收到的消息
     * @param session session
     */
    @OnMessage
    public synchronized void onMessage(String message, Session session) throws Exception {

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
        Optional.ofNullable(session).ifPresent(session1 -> sessionCodeMap.put(session.getId(), message1.getModulecode()));
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
    private void restHandle(Message message) throws ParseException {

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
    private void assemble(Message message) throws ParseException {
        //判断是什么设备
        Optional.ofNullable(mqMessageService).orElseGet(() -> mqMessageService = ApplicationContextUtils.get(MqMessageService.class));
        MqMessage mqMessageSelect = new MqMessage();
        mqMessageSelect.setModulecode(message.getModulecode());
        MqMessage mqMessage = mqMessageService.selectOne(mqMessageSelect);
        if (mqMessage != null){
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
                default:
            }
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
            Optional.ofNullable(stationService).orElseGet(() -> stationService = ApplicationContextUtils.get(StationService.class));
            //查询所有的公交站
            DeviceStationService deviceLoopService = ApplicationContextUtils.get(DeviceStationService.class);
            List<DeviceStation> deviceLoops = deviceLoopService.selectAll();
            if (deviceLoops != null) {
                deviceLoops.stream().map(DeviceStation::getStationid).forEach(stationid -> {
                    DevicesMsg devicesMsg = new DevicesMsg();
                    devicesMsg.setStationid(stationid);
                    devicesMsg.setStationname(stationService.selectByPrimaryKey(stationid).getStationName());
                    //添加lcd设备
                    List<Devices> devices = new ArrayList<>();
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
                send(code, platformStatis(messageVo));
                log.info("站台定时任务时间 : {}", messageVo.getTimestamp());
            }
        }
    }

    /**
     * 拼装站台统计信息
     *
     * @param messageVo 要拼装的消息
     * @return 拼装好的消息
     */
    @SuppressWarnings("Duplicates")
    private String platformStatis(MessageVo messageVo) throws ParseException {

        platformStatisVo platformStatisVo = new platformStatisVo();
        Optional.ofNullable(deviceLoopService).orElseGet(() -> deviceLoopService = ApplicationContextUtils.get(DeviceLoopService.class));
        DeviceLoop deviceLoopSelect = new DeviceLoop();
        StatisticsVo statisticsVo = new StatisticsVo();
        //本月
        float currentmonth = 0;
        //上月
        float lastmonth = 0;
        //本年
        float thisyear = 0;

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
                DeviceLoop deviceLoop = deviceLoopService.selectOne(deviceLoopSelect);
                if (deviceLoop != null){
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
                DeviceLoop deviceLoop = deviceLoopService.selectOne(deviceLoopSelect);
                if (deviceLoop != null){
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
        messageVo.setMsg(platformStatisVo);
        return JSON.toJSONString(messageVo);
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
            DeviceLoop deviceLoop = deviceLoopService.selectOne(new DeviceLoop(loopStateDto.getLoop()));
            deviceStationSelect.setDeviceDid(deviceLoop.getDeviceDid());
            DeviceStation deviceStation = deviceStationService.selectOne(deviceStationSelect);
            if (deviceStation != null) {
                if (stationid.equals(deviceStation.getStationid())) {
                    Devices device = new Devices(loopStateDto);
                    devices.add(device);
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
            return tStatusService.findAllStatusByRedis();
        });
        //判断该设备是否在该网关下
        allStatus.stream().filter(iotLedStatus -> stationid.equals(deviceStationService.selectOne(new DeviceStation(iotLedStatus.getDid())).getStationid()))
                .forEach(iotLcdStatus -> {
                    Devices device = new Devices(iotLcdStatus);
                    devices.add(device);
                });
        devicesMsg.setDevices(devices);

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
        allStatusByRedis.stream().filter(iotLcdStatus -> stationid.equals(deviceStationService.selectOne(new DeviceStation(iotLcdStatus.getId())).getStationid()))
                .forEach(iotLcdStatus -> {
                    Devices device = new Devices(iotLcdStatus);
                    devices.add(device);
                });
        devicesMsg.setDevices(devices);
    }

    /**
     * 照明首次连接 同时也定时推送
     */
    @Scheduled(cron = "${send.light-cron}")
    public void light() throws ParseException {
        Integer modulecode = getModuleCode("light");
        String code = String.valueOf(modulecode);
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            messageVo.setMsgcode(203001);
            //调用接口 获取当前照明状态
            Optional.ofNullable(loopStatusServiceApi).orElseGet(() -> loopStatusServiceApi = ApplicationContextUtils.get(LoopStatusServiceApi.class));
            loopStatus = loopStatusServiceApi.findLoopStatus();
            List<Lights> lightsList = new ArrayList<>();
            //保存设备状态信息
            LightMsgState lightMsgState = new LightMsgState();
            if (loopStatus != null && loopStatus.size() != 0) {
                //灯箱设备
                List<Lights> lamphouses = new ArrayList<>();
                //顶棚
                List<Lights> platfonds = new ArrayList<>();
                //log
                List<Lights> logos = new ArrayList<>();
                loopStatus.forEach(tLoopStateDto -> {
                    //判断这个回路下是什么设备
                    Lights light = new Lights(tLoopStateDto);
                    lightsList.add(light);

                    //判断这是什么设备
                    if (light.getLamphouseid() != null) {
                        lamphouses.add(light);
                    }
                    if (light.getPlatfondid() != null) {
                        platfonds.add(light);
                    }
                    if (light.getLogoid() != null) {
                        logos.add(light);
                    }
                });
                LightMsg lightMsg = new LightMsg(lightsList);
                messageVo.setMsg(lightMsg);
                send(code, JSON.toJSONString(messageVo));
                //推送设备状态信息
                lightMsgState.setLamphouses(lamphouses);
                lightMsgState.setPlatfonds(platfonds);
                lightMsgState.setLogos(logos);
                messageVo.setMsg(lightMsgState);
                messageVo.setMsgcode(203004);
                send(code, JSON.toJSONString(messageVo));

                //推送统计信息
                StatisticsMsgVo statisticsMsgVo = lightStatis(loopStatus);
                messageVo.setMsg(statisticsMsgVo);
                messageVo.setMsgcode(203002);
                send(code, JSON.toJSONString(messageVo));
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

        List<String> dids = new ArrayList<>();
        //取出所有的did
        loopStatus.forEach(loopStateDto -> {
            //通过回路号查询这个是什么设备
            DeviceLoopService deviceLoopService = ApplicationContextUtils.get(DeviceLoopService.class);
            DeviceLoop deviceLoopSelect = new DeviceLoop();
            deviceLoopSelect.setLoop(loopStateDto.getLoop());
            DeviceLoop deviceLoop = deviceLoopService.selectOne(deviceLoopSelect);
            dids.add(String.valueOf(deviceLoop.getDeviceDid()));

        });
        //统计
        return equipStatis(dids);
    }


    /**
     * lcd首次连接信息 也需要定时向前台推送
     */
    @Scheduled(cron = "${send.lcd-cron}")
    public void lcd() throws ParseException {
        //查出led的 moduleCode
        Integer modulecode = getModuleCode("lcd");
        String code = String.valueOf(modulecode);
        //判断该连接是要被关闭
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            //调用接口 获得所有站屏的设备状态
            messageVo.setMsgcode(204001);
            Optional.ofNullable(iotLcdStatusService).orElseGet(() -> iotLcdStatusService = ApplicationContextUtils.get(IotLcdsStatusService.class));
            this.allStatusByRedis = iotLcdStatusService.findAllStatusByRedis();
            if (allStatusByRedis != null && allStatusByRedis.size() != 0) {
                Lcds lcds = new Lcds(allStatusByRedis);
                LcdMsg lcdMsg = new LcdMsg(lcds);
                messageVo.setMsg(lcdMsg);
                send(code, JSON.toJSONString(messageVo));

                //推送设备状态 将多余字段设置为 null
                allStatusByRedis.forEach(iotLcdStatus -> iotLcdStatus.setVolume(null));
                Lcds lcds2 = new Lcds(allStatusByRedis);
                LcdMsg lcdMsg2 = new LcdMsg(lcds2);
                messageVo.setMsg(lcdMsg2);
                messageVo.setMsgcode(204004);
                send(code, JSON.toJSONString(messageVo));

                //推送统计信息
                StatisticsMsgVo statisticsMsgVo = lcdStatis(allStatusByRedis);
                messageVo.setMsg(statisticsMsgVo);
                messageVo.setMsgcode(204002);
                send(code, JSON.toJSONString(messageVo));

                //推送lcd设备统计信息
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
        if (sessions != null){
            for (Session session : sessions) {
                if (StringUtils.equals(code,String.valueOf(sessionCodeMap.get(session.getId())))){
                    return true;
                }
            }
        }
        return false;
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
        return equipStatis(dids);
    }

    /**
     * led首次连接信息 也需要定时向前台推送
     */
    @Scheduled(cron = "${send.led-cron}")
    public void led() throws ParseException {
        //查出led的 moduleCode
        Integer modulecode = getModuleCode("led");
        String code = String.valueOf(modulecode);
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            messageVo.setMsgcode(205001);
            //调用接口
            Optional.ofNullable(tStatusService).orElseGet(() -> tStatusService = ApplicationContextUtils.get(TStatusService.class));
            allStatus = tStatusService.findAllStatusByRedis();
            if (allStatus != null && allStatus.size() != 0) {
                Leds leds = new Leds(allStatus);
                LedMsg ledMsg = new LedMsg(leds);
                messageVo.setMsg(ledMsg);
                send(code, JSON.toJSONString(messageVo));

                allStatus.forEach(tStatusDto -> {
                    tStatusDto.setVolume(null);
                    tStatusDto.setLight(null);
                });
                Leds leds2 = new Leds(allStatus);
                LedMsg ledMsg2 = new LedMsg(leds2);
                messageVo.setMsg(ledMsg2);
                messageVo.setMsgcode(205004);
                send(code, JSON.toJSONString(messageVo));

                //推送统计信息
                StatisticsMsgVo statisticsMsgVo = ledStatis(allStatus);
                messageVo.setMsg(statisticsMsgVo);
                messageVo.setMsgcode(205002);
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
        return equipStatis(dids);

    }


    /**
     * 关闭连接的方法
     *
     * @param session session
     */
    @OnClose
    public synchronized static  void onClose(Session session) {
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
    public synchronized void send(String token, String message) {

        if (StringUtils.isNotBlank(token)) {
            //遍历session推送消息到页面
            Optional.ofNullable(SESSION_MAP.get(token)).ifPresent(session -> {
                if (session.size() != 0) {
                    session.forEach(session1 -> {
                        try {
                            session1.getBasicRemote().sendText(message);
                        } catch (Exception e) {
                            //将该session关闭
                            onClose(session1);
                            log.error("消息推送失败 : {}", e.getMessage());
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
     * @param dids 设备did
     * @return 统计信息
     * @throws ParseException 时间格式化异常
     */
    private StatisticsMsgVo equipStatis(List<String> dids) throws ParseException {

        Optional.ofNullable(deviceLoopService).orElseGet(() -> deviceLoopService = ApplicationContextUtils.get(DeviceLoopService.class));
        //遍历通过did查出回路
        DeviceLoop deviceLoopSelect = new DeviceLoop();
        //本月
        float currentmonth = 0;
        //上月
        float lastmonth = 0;
        //本年
        float thisyear = 0;
        for (String did : dids) {
            if (StringUtils.isNotBlank(did)) {
                deviceLoopSelect.setDeviceDid(did);
                DeviceLoop deviceLoop = deviceLoopService.selectOne(deviceLoopSelect);
                //查出单个设备的统计信息
                if (deviceLoop != null){
                    StatisticsVo statisticsVoSelect = new StatisticsVo();
                    statisticsVoSelect.setDid(String.valueOf(deviceLoop.getGatewayDid()));
                    statisticsVoSelect.setLoop(deviceLoop.getLoop());
                    StatisticsMsgVo statistics = Statistics.findStatistics(statisticsVoSelect);
                    currentmonth = currentmonth + statistics.getCurrentmonth();
                    lastmonth = lastmonth + statistics.getLastmonth();
                    thisyear = thisyear + statistics.getThisyear();
                }
            }
        }
        return new StatisticsMsgVo(currentmonth, lastmonth, thisyear);
    }
}
