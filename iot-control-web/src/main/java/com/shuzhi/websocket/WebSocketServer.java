package com.shuzhi.websocket;


import com.alibaba.fastjson.JSON;
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
import com.shuzhi.light.entities.TLoopStateDto;
import com.shuzhi.light.service.LoopStatusServiceApi;
import com.shuzhi.lightiotcomm.entities.*;
import com.shuzhi.lightiotcomm.service.LightIotCommServiceApi;
import com.shuzhi.mapper.StationMapper;
import com.shuzhi.rabbitmq.Message;
import com.shuzhi.service.GroupService;
import com.shuzhi.service.LightpoleDevService;
import com.shuzhi.service.MqMessageService;
import com.shuzhi.service.StationService;
import com.shuzhi.service.serviceimpl.GroupDeviceServiceImpl;
import com.shuzhi.service.serviceimpl.GroupServiceImpl;
import com.shuzhi.service.serviceimpl.LighpoleServiceImpl;
import com.shuzhi.service.serviceimpl.LightpoleDevServiceImpl;
import com.shuzhi.spon.entitys.Bcsts;
import com.shuzhi.spon.entitys.Offlines;
import com.shuzhi.spon.entitys.TEventMessage;
import com.shuzhi.spon.service.TEventSponService;
import com.shuzhi.utils.ConstantUtils;
import com.shuzhi.websocket.socketvo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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
    ObjectMapper objectMapper = new ObjectMapper();
    private static StringRedisTemplate redisTemplate;

    private MqMessageService mqMessageService;

    private TStatusService tStatusService;

    private LoopStatusServiceApi loopStatusServiceApi;

    private IotLcdsStatusService iotLcdStatusService;

    private LightIotCommServiceApi lightIotCommServiceApi;

    //private DeviceStationService deviceStationService;

    private StationService stationService;

    private TEventSponService tEventSponService;

    //private DeviceLoopMapper deviceLoopMapper;

    //private TLightPoleMapper tLightPoleMapper;

    private TEventLedService tEventLedService;

    private TEventLcdService tEventLcdService;

    private LightpoleDevService lightpoleDevService;

    private LighpoleServiceImpl lighpoleService;

    private GroupServiceImpl groupService;

    private GroupDeviceServiceImpl groupDeviceService;

    private StationMapper stationMapper;

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
    private List<TLoopState> loopStatus;
    private List<ControllerApi> controllerStatus;
    private List<LoopRedis> loopOffline;
    private List<GatewayRedis> findGatewayOffline;

    /**
     * 环测设备状态
     */
    private List<TDataDto> dataAllStatus;
    /**
     * 广播状态
     */
    private List<Bcsts> bcstsList;
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
                case "environment":
                    //调用环测
                    frt();
                    break;
                case "broadcast":
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
     * 照明首次连接 同时也定时推送
     */
    @Scheduled(cron = "${send.light-cron}")
    public void light() throws ParseException, JsonProcessingException {

        Integer modulecode = getModuleCode("light");
        String code = String.valueOf(modulecode);
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            messageVo.setMsgcode(220001);
            //调用接口 获取当前照明状态
            Optional.ofNullable(loopStatusServiceApi).orElseGet(() -> loopStatusServiceApi = ApplicationContextUtils.get(LoopStatusServiceApi.class));
            Optional.ofNullable(lightpoleDevService).orElseGet(() -> lightpoleDevService = ApplicationContextUtils.get(LightpoleDevService.class));
            Optional.ofNullable(lighpoleService).orElseGet(() -> lighpoleService = ApplicationContextUtils.get(LighpoleServiceImpl.class));
            Optional.ofNullable(lightIotCommServiceApi).orElseGet(() -> lightIotCommServiceApi = ApplicationContextUtils.get(LightIotCommServiceApi.class));
            Optional.ofNullable(groupService).orElseGet(() -> groupService = ApplicationContextUtils.get(GroupServiceImpl.class));
            Optional.ofNullable(groupDeviceService).orElseGet(() -> groupDeviceService = ApplicationContextUtils.get(GroupDeviceServiceImpl.class));

           // List<TGateway> tGateways = lightIotCommServiceApi.gatewayLocationInof();
            controllerStatus = lightIotCommServiceApi.findControllerStatus();
            //查询回路通过breakerID 查询 LoopRedis 得到 回路 在线离线状态
            loopStatus = lightIotCommServiceApi.findLoopStatus();
            loopOffline = lightIotCommServiceApi.findLoopOffline();
            List<Loops> loops = new ArrayList<>();

            if (loopStatus != null && loopStatus.size() != 0) {
//                //所有单灯信息
//                loopStatus.forEach(tLoopStateDto -> {
//                    Loops loops1 = new Loops();
//                    //设备的did去查询灯杆对应设备
//                    LightpoleDev lightpoleDev = new LightpoleDev();
//                    lightpoleDev.setDeviceId(Integer.parseInt(tLoopStateDto.getId().toString()));
//                    lightpoleDev.setDeviceType(7);
//                    LightpoleDev lightpoleDev1 = lightpoleDevService.selectOne(lightpoleDev);
//                    //根据灯灯杆id查询灯杆信息
//                    Lighpole lighpole = new Lighpole();
//                    lighpole.setLamppostid(lightpoleDev1.getLamppostid());
//
//                    Lighpole lighpole1 = lighpoleService.selectOne(lighpole);
//
//                    List<Lights> light = new ArrayList<>();
//                    List<Lampposts> lamppostsList = new ArrayList<>();
//
//                    loops1.setLoopid(tLoopStateDto.getId().intValue());
//                    loops1.setLoopnum(tLoopStateDto.getBreakerID());
//                    loops1.setLoopname(tLoopStateDto.getBreakerName());
//
//                    // 根据loopRedis.getLoopid() == tLoopStateDto.getBreakerID() 获取 在线和离线状态
//                    loopOffline.forEach(loopRedis -> {
//                        if (loopRedis.getLoopid() == tLoopStateDto.getBreakerID()) {
//                            loops1.setOnoff(loopRedis.getOnoff());
//                            loops1.setState(tLoopStateDto.getStatus());
//                        }
//                    });
//                    //获取灯杆信息
//                    Lights lights = new Lights();
//                    Lampposts lampposts = new Lampposts();
//                    lampposts.setLamppostid(lighpole1.getLamppostid());
//                    lampposts.setLamppostname(lighpole1.getLamppostname());
//
//                    controllerStatus.forEach(controllerStatus1 -> {
//                        lights.setName(controllerStatus1.getName());
//                        lights.setState(controllerStatus1.getOnoff());
//                        lights.setOnoff(controllerStatus1.getOnline());// TODO  修改在线状态
//                        /*if (controllerStatus1.getOnline().equals("N")) { // TODO  修改在线状态
//                            lights.setOnoff(0);
//                        } else {
//                            lights.setOnoff(1);
//                        }*/
//                        lights.setId(controllerStatus1.getId().intValue());
//                        light.add(lights);
//                        lampposts.setLights(light);
//                        lamppostsList.add(lampposts);
//                    });
//                    loops1.setLampposts(lamppostsList);
//                    loops.add(loops1);
//                });
//                LightMsg lightMsg = new LightMsg(loops);
//                messageVo.setMsg(lightMsg);
////                send(code, JSON.toJSONString(messageVo, SerializerFeature.DisableCircularReferenceDetect));
//                send(code, objectMapper.writeValueAsString(messageVo));

                //所有单灯信息（分组）
                List<Groups> groups = new ArrayList<>();
                Group group2 = new Group();
                group2.setDeviceType(7);
                List<Group> groupList = groupService.select(group2);
                if (!groupList.isEmpty()) {
                    groupList.forEach(group -> {
                        Groups groups1 = new Groups();
                        groups1.setGroupid(group.getId().intValue());
                        groups1.setGroupname(group.getGroupName());
                        //灯杆数组
                        List<Lampposts> lamppostsList = new ArrayList<>();
                        List<Lighpoles>  LighpolesLists = lighpoleService.findAlls();
                        //根据组id 查询关联表的设备id
//                        LightpoleDev lightpoleDev = new LightpoleDev();
//                        lightpoleDev.setDeviceType(7);
//                        List<LightpoleDev> lightpoleDevList = lightpoleDevService.select(lightpoleDev);
                        //遍历查询灯杆信息
                           LighpolesLists.forEach(lighpoles -> {
                            //根据设备和灯杆关系表查询灯杆信息
                            Lighpole lighpole = new Lighpole();
                            lighpole.setLamppostid(lighpoles.getLamppostid());
                           // Lighpole lighpole1 = lighpoleService.selectOne(lighpole);
                            //set灯杆信息
                            Lampposts lampposts = new Lampposts();
                            lampposts.setLamppostid(lighpoles.getLamppostid());
                            lampposts.setLamppostname(lighpoles.getLamppostname());
                            // 遍历set单灯信息
                            List<Lights> light = new ArrayList<>();
                            controllerStatus.forEach(controllerStatus1 -> {
                                if(controllerStatus1.getId() == Long.parseLong(lighpoles.getId().toString())) {
                                    Lights lights = new Lights();
                                    lights.setId(controllerStatus1.getId().intValue());
                                    lights.setName(controllerStatus1.getName());
                                    lights.setOnoff(controllerStatus1.getOnline());// TODO  修改在线状态
                                /*if (controllerStatus1.getComm().equals("N")) {     // TODO  修改在线状态                           /*if (controllerStatus1.getComm().equals("N")) {
                                    lights.setOnoff(0);
                                } else {
                                    lights.setOnoff(1);
                                }*/
                                    lights.setState(controllerStatus1.getOnoff());
                                    light.add(lights);
                                }
                            });
                            lampposts.setLights(light);
                            lamppostsList.add(lampposts);
                        });
                        groups1.setLampposts(lamppostsList);
                        groups.add(groups1);
                    });
                }
                LightsMsg lightsMsg = new LightsMsg(groups);
                messageVo.setMsg(lightsMsg);
                messageVo.setMsgcode(220002);
                send(code, objectMapper.writeValueAsString(messageVo));

                //推送统计信息
                StatisticsMsgsVo statisticsMsgsVo = new StatisticsMsgsVo();
                statisticsMsgsVo.addLightNum(controllerStatus);
                messageVo.setMsg(statisticsMsgsVo);
                messageVo.setMsgcode(220003);
                send(code, objectMapper.writeValueAsString(messageVo));

                //推送离线设备信息
                messageVo.setMsgcode(220004);
                OfflineMsg offlineMsg = new OfflineMsg();
                offlineMsg.offlineLightMsg(controllerStatus);
                messageVo.setMsg(offlineMsg);
                send(code, objectMapper.writeValueAsString(messageVo));

                //所有集中控制器信息
                findGatewayOffline = lightIotCommServiceApi.findGatewayOffline();
                Group group = new Group();
                group.setDeviceType(7);
                List<Group> groupList1 = groupService.select(group);
                List<Controllers> controllersList = new ArrayList<>();
                if (groupList1.isEmpty()) {
                    groupList.forEach(groupDevice -> {
                        findGatewayOffline.forEach(findGatewayOfflines -> {
                            //所有集中控制器信息
                            Controllers controllers = new Controllers();
                            controllers.setControllerid(findGatewayOfflines.getGatewayId().toString());
                            controllers.setControllername(groupDevice.getGroupName());
                            controllers.setControllernum(findGatewayOfflines.getDid());
                            controllers.setState(findGatewayOfflines.getOnline());
                            //set回路数据
                            //所有单灯信息
                            List<Loops> list = new ArrayList<Loops>();
                            loopStatus.forEach(tLoopStateDto -> {
                                Loops loops1 = new Loops();
                                if (tLoopStateDto.getCcuUid().equals(findGatewayOfflines.getDid())) {
                                    loops1.setLoopid(tLoopStateDto.getId().intValue());
                                    loops1.setLoopname(tLoopStateDto.getBreakerName());
                                    loops1.setLoopnum(tLoopStateDto.getBreakerID());
                                    loops1.setOnoff(tLoopStateDto.getStatus());
                                }
                                list.add(loops1);
                            });
                            controllers.setLoopsList(list);
                        });
                    });
                }
                messageVo.setMsgcode(220005);
                messageVo.setMsg(controllersList);
                send(code, objectMapper.writeValueAsString(messageVo));
                log.info("照明定时任务时间 : {}", messageVo.getTimestamp());
            }
        }
    }

    /**
     * lcd首次连接信息 也需要定时向前台推送
     */
    @Scheduled(cron = "${send.lcd-cron}")
    public void lcd() throws ParseException, JsonProcessingException {
        //查出led的 moduleCode
        Integer modulecode = getModuleCode("lcd");
        String code = String.valueOf(modulecode);
        //判断该连接是要被关闭
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            //调用接口 获得所有站屏的设备状态
            messageVo.setMsgcode(230001);
            Optional.ofNullable(tEventLcdService).orElseGet(() -> tEventLcdService = ApplicationContextUtils.get(TEventLcdService.class));
            Optional.ofNullable(iotLcdStatusService).orElseGet(() -> iotLcdStatusService = ApplicationContextUtils.get(IotLcdsStatusService.class));
            Optional.ofNullable(groupService).orElseGet(() -> groupService = ApplicationContextUtils.get(GroupServiceImpl.class));
            Optional.ofNullable(lightpoleDevService).orElseGet(() -> lightpoleDevService = ApplicationContextUtils.get(LightpoleDevServiceImpl.class));
            Optional.ofNullable(lighpoleService).orElseGet(() -> lighpoleService = ApplicationContextUtils.get(LighpoleServiceImpl.class));

            allStatusByRedis = iotLcdStatusService.findAllStatusByRedis();
            if (allStatusByRedis != null && allStatusByRedis.size() != 0) {
                List<GroupsLcd> groupsLcds = new ArrayList<>();
                //查询所有组
                List<Group> groups = groupService.selectAll();
                groups.forEach(group -> {
                    //根据组设备类型查询灯杆信息
                    LightpoleDev lightpoleDev = new LightpoleDev();
                    lightpoleDev.setDeviceType(group.getDeviceType());
                    List<LightpoleDev> lightpoleDevs = lightpoleDevService.select(lightpoleDev);

                    GroupsLcd groupsLcd = new GroupsLcd();
                    groupsLcd.setGroupid(group.getId().intValue());
                    groupsLcd.setGroupname(group.getGroupName());

                    List<Lcdss> lcdsses = new ArrayList<>();
                    lightpoleDevs.forEach(lightpoleDev1 -> allStatusByRedis.forEach(iotLcdStatusTwo -> {
                        //根据设备id查询灯杆
                        Lighpole lighpole = new Lighpole();
                        lighpole.setLamppostid(lightpoleDev1.getLamppostid());
                        Lighpole lighpole1 = lighpoleService.selectOne(lighpole);

                        if (lighpole1 != null) {
                            if (lighpole1.getLamppostid().equals(lightpoleDev1.getLamppostid())) {
                                Lcdss lcdss = new Lcdss();
                                lcdss.setLamppostid(lighpole1.getLamppostid());
                                lcdss.setLamppostname(lighpole1.getLamppostname());
                                try {
                                    LcdConversionUtil.fatherToChild(iotLcdStatusTwo, lcdss);
                                    lcdsses.add(lcdss);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            groupsLcd.setLcds(lcdsses);
                        }
                    }));
                    if (groupsLcd.getLcds().size() != 0) {
                        groupsLcds.add(groupsLcd);
                    }
                });
                GroupsLcdMsg groupsLcdMsg = new GroupsLcdMsg(groupsLcds);
                messageVo.setMsg(groupsLcdMsg);
                send(code, objectMapper.writeValueAsString(messageVo));

                //推送统计信息
                LightMsgVo lightMsgVo = new LightMsgVo();
                lightMsgVo.lightMsgVoLcd(allStatusByRedis, tEventLcdService.findCountByTime());
                messageVo.setMsg(lightMsgVo);
                messageVo.setMsgcode(230003);
                send(code, JSON.toJSONString(messageVo));

                //推送离线设备信息
                messageVo.setMsgcode(230004);
                OfflineMsg offlineMsg = new OfflineMsg();
                offlineMsg.offlineLcdMsg(allStatusByRedis);//TODO 判断离线设备
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
     * led首次连接信息 也需要定时向前台推送
     */
    @Scheduled(cron = "${send.led-cron}")
    public void led() throws ParseException, JsonProcessingException {
        //查出led的 moduleCode
        Integer modulecode = getModuleCode("led");
        String code = String.valueOf(modulecode);
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            messageVo.setMsgcode(230011);
            //调用接口
            Optional.ofNullable(tEventLedService).orElseGet(() -> tEventLedService = ApplicationContextUtils.get(TEventLedService.class));
            Optional.ofNullable(tStatusService).orElseGet(() -> tStatusService = ApplicationContextUtils.get(TStatusService.class));
            Optional.ofNullable(groupService).orElseGet(() -> groupService = ApplicationContextUtils.get(GroupServiceImpl.class));
            Optional.ofNullable(lightpoleDevService).orElseGet(() -> lightpoleDevService = ApplicationContextUtils.get(LightpoleDevServiceImpl.class));
            Optional.ofNullable(lighpoleService).orElseGet(() -> lighpoleService = ApplicationContextUtils.get(LighpoleServiceImpl.class));
            Optional.ofNullable(groupDeviceService).orElseGet(() -> groupDeviceService = ApplicationContextUtils.get(GroupDeviceServiceImpl.class));

            allStatus = tStatusService.findAllStatusByRedis();
            if (allStatus != null && allStatus.size() != 0) {
                List<GroupsLed> groupsLeds = new ArrayList<>();
                //查询根据type组
                Group group = new Group();
                group.setDeviceType(ConstantUtils.KYE_LED);
                List<Group> groups = groupService.select(group);

                groups.forEach(group1 -> {
                    //根据组和设备的关联表数据
                    GroupDevice groupDevice = new GroupDevice();
                    groupDevice.setGroupId(group1.getId());
                    List<GroupDevice> groupDevices = groupDeviceService.select(groupDevice);
                    //组
                    GroupsLed groupsLed = new GroupsLed();
                    groupsLed.setGroupid(group1.getId().intValue());
                    groupsLed.setGroupname(group1.getGroupName());
                    List<Ledss> ledsses = new ArrayList<>();
                    for (GroupDevice device : groupDevices) {
                        //根据设备的id查询灯杆和设备关联表
                        LightpoleDev lightpoleDev = new LightpoleDev();
                        lightpoleDev.setDeviceId(device.getDeviceId());
                        lightpoleDev.setDeviceType(ConstantUtils.KYE_LED);
                        LightpoleDev lightpoleDev2 = lightpoleDevService.selectOne(lightpoleDev);
                        if (lightpoleDev2 == null) {
                            continue;
                        }
                        allStatus.forEach(tStatusDto -> {
                            if (tStatusDto.getId().equals(device.getDeviceId())) {
                                //根据设备id查询灯杆
                                Lighpole lighpole = new Lighpole();
                                lighpole.setLamppostid(lightpoleDev2.getLamppostid());
                                Lighpole lighpole1 = lighpoleService.selectOne(lighpole);
                                if (lighpole1 != null) {
                                    if (lighpole1.getLamppostid().equals(lightpoleDev2.getLamppostid())) {
                                        Ledss ledss = new Ledss();
                                        ledss.setLamppostid(lighpole1.getLamppostid());
                                        ledss.setLamppostname(lighpole1.getLamppostname());
                                        try {
                                            LcdConversionUtil.fatherToChild(tStatusDto, ledss);
                                            ledsses.add(ledss);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    groupsLed.setLeds(ledsses);
                                }
                            }
                        });

                    }
                    if (groupsLed.getLeds() != null) {
                        groupsLeds.add(groupsLed);
                    }
                });
                GroupsLedMsg groupsLedMsg = new GroupsLedMsg(groupsLeds);
                messageVo.setMsg(groupsLedMsg);
                try {
                    send(code, objectMapper.writeValueAsString(messageVo));
                    log.info("led发送的数据内容: {}", messageVo);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }


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
     * frt环测首次建连
     */
    @Scheduled(cron = "${send.frt-cron}")
    private void frt() throws ParseException {
        //查出frt的 moduleCode
        Integer modulecode = getModuleCode("environment");
        String code = String.valueOf(modulecode);
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            messageVo.setMsgcode(240001);
            //调用接口
            Optional.ofNullable(deviceClientService).orElseGet(() -> deviceClientService = ApplicationContextUtils.get(DeviceClientService.class));
            Optional.ofNullable(lightpoleDevService).orElseGet(() -> lightpoleDevService = ApplicationContextUtils.get(LightpoleDevService.class));
            Optional.ofNullable(lighpoleService).orElseGet(() -> lighpoleService = ApplicationContextUtils.get(LighpoleServiceImpl.class));
            Optional.ofNullable(dataClientService).orElseGet(() -> dataClientService = ApplicationContextUtils.get(DataClientService.class));

            dataAllStatus = dataClientService.findAllStatus();
            if (dataAllStatus != null && dataAllStatus.size() != 0) {
                for (TDataDto status : dataAllStatus) {
                    //根据设备did查找灯杆id
                    if (status.getDevicecode() != null && status.getName() != null) {
                        //根据did查询关联表灯杆id
                        LightpoleDev lightpoleDev = new LightpoleDev();
                        lightpoleDev.setDeviceId(status.getId());
                        lightpoleDev.setDeviceType(5);
                        LightpoleDev lightpoleDev1 = lightpoleDevService.selectOne(lightpoleDev);
                        if (lightpoleDev1 != null) {
                            //查询灯杆信息
                            Lighpole lighpole = new Lighpole();
                            lighpole.setLamppostid(lightpoleDev1.getLamppostid());
                            Lighpole lighpole1 = lighpoleService.selectOne(lighpole);
                            status.setLamppostid(lighpole1.getLamppostid());
                            status.setLamppostname(lighpole1.getLamppostname());
                        }
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
    @Scheduled(cron = "${send.spon-cron}")
    private void spon() throws ParseException {
        //查出spon的 moduleCode
        Integer modulecode = getModuleCode("broadcast");
        String code = String.valueOf(modulecode);
        if (isOnClose(code)) {
            MessageVo messageVo = setMessageVo(modulecode);
            messageVo.setMsgcode(230015);
            Optional.ofNullable(tEventSponService).orElseGet(() -> tEventSponService = ApplicationContextUtils.get(TEventSponService.class));
            Optional.ofNullable(deviceClientService).orElseGet(() -> deviceClientService = ApplicationContextUtils.get(DeviceClientService.class));
            Optional.ofNullable(groupService).orElseGet(() -> groupService = ApplicationContextUtils.get(GroupServiceImpl.class));
            Optional.ofNullable(lightpoleDevService).orElseGet(() -> lightpoleDevService = ApplicationContextUtils.get(LightpoleDevServiceImpl.class));
            Optional.ofNullable(lighpoleService).orElseGet(() -> lighpoleService = ApplicationContextUtils.get(LighpoleServiceImpl.class));

            bcstsList = tEventSponService.findByType();
            if (bcstsList != null && bcstsList.size() != 0) {
                //查询所有组
                List<Group> groups = groupService.selectAll();
                List<Spons> sponsList = new ArrayList<>();
                groups.forEach(group -> {
                    //根据组设备类型查询灯杆信息
                    LightpoleDev lightpoleDev = new LightpoleDev();
                    lightpoleDev.setDeviceType(group.getDeviceType());
                    List<LightpoleDev> lightpoleDevs = lightpoleDevService.select(lightpoleDev);

                    GroupsLcd groupsLcd = new GroupsLcd();
                    groupsLcd.setGroupid(group.getId().intValue());
                    groupsLcd.setGroupname(group.getGroupName());

                    List<Lcdss> lcdsses = new ArrayList<>();
                    lightpoleDevs.forEach(lightpoleDev1 -> bcstsList.forEach(bcsts -> {
                        //根据设备id查询灯杆
                        Lighpole lighpole = new Lighpole();
                        lighpole.setLamppostid(lightpoleDev1.getLamppostid());
                        Lighpole lighpole1 = lighpoleService.selectOne(lighpole);

                        if (lighpole1 != null) {
                            if (lighpole1.getLamppostid().equals(lightpoleDev1.getLamppostid())) {
                                Spons spons = new Spons();
                                spons.setGroupid(group.getId().intValue());
                                spons.setGroupname(group.getGroupName());
                                spons.setLamppostid(lighpole1.getLamppostid());
                                spons.setLamppostname(lighpole1.getLamppostname());
                                try {
                                    LcdConversionUtil.fatherToChild(bcsts, spons);
                                    sponsList.add(spons);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }));
                });
                SponsMsg sponsMsg = new SponsMsg(sponsList);
                messageVo.setMsg(sponsMsg);
                send(code, JSON.toJSONString(messageVo));

                //推送广播统计
                List<TEventMessage> byTime = tEventSponService.findByTime();
                messageVo.setMsg(byTime);
                messageVo.setMsgcode(230016);
                send(code, JSON.toJSONString(messageVo));

                //离线设备信息
                List<Offlines> offlines = tEventSponService.findOfflines();
                messageVo.setMsg(offlines);
                messageVo.setMsgcode(230017);
                send(code, JSON.toJSONString(messageVo));
            }
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
