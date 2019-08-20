package com.shuzhi.websocket;

import com.alibaba.fastjson.JSON;
import com.shuzhi.common.utils.WrapMapper;
import com.shuzhi.common.utils.Wrapper;
import com.shuzhi.config.service.MsgCodeService;
import com.shuzhi.lcd.entities.IotHobo;
import com.shuzhi.lcd.service.LcdClientService;
import com.shuzhi.led.entities.TLed;
import com.shuzhi.led.service.LedClientService;
import com.shuzhi.lightiotcomm.entities.TController;
import com.shuzhi.lightiotcomm.entities.TGateway;
import com.shuzhi.lightiotcomm.entities.TLoop;
import com.shuzhi.lightiotcomm.service.LightIotCommServiceApi;
import com.shuzhi.rabbitmq.RabbitProducer;
import com.shuzhi.utils.ConstantUtils;
import com.shuzhi.websocket.socketvo.MessageVo;
import com.shuzhi.websocket.socketvo.Msg;
import com.shuzhi.websocket.socketvo.SimpleProtocolVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author zgk
 * @description 接收命令信息
 * @date 2019-07-11 13:39
 */
@SuppressWarnings("AlibabaUndefineMagicConstant")
@Slf4j
@RestController
@RequestMapping("/websocket")
public class WebSocketController {

    private final RabbitProducer rabbitProducer;

    private final StringRedisTemplate redisTemplate;

    private final MsgCodeService msgCodeService;

    private final LightIotCommServiceApi lightIotCommServiceApi;
    private LedClientService ledClientService;

    private LcdClientService lcdClientService;

    public WebSocketController(RabbitProducer rabbitProducer,LightIotCommServiceApi lightIotCommServiceApi, StringRedisTemplate redisTemplate, MsgCodeService msgCodeService,LedClientService ledClientService,LcdClientService lcdClientService) {
        this.rabbitProducer = rabbitProducer;
        this.redisTemplate = redisTemplate;
        this.lightIotCommServiceApi = lightIotCommServiceApi;
        this.msgCodeService = msgCodeService;
        this.ledClientService = ledClientService;
        this.lcdClientService = lcdClientService;
    }

    /**
     * 命令操作
     *
     * @param messageVo 命令详情
     * @return 操作结果
     */
    @RequestMapping("/command")
    public Wrapper command(@RequestBody MessageVo messageVo) {

        if (messageVo.getModulecode() == null){
            return WrapMapper.wrap(500, "Modulecode不能为空");
        }
        //拼装消息
        List<SimpleProtocolVo> messageList = assemble(messageVo);
        //遍历并发送简易协议
        Objects.requireNonNull(messageList).forEach(message -> new SynSend(rabbitProducer, message, messageVo.getModulecode(), redisTemplate).start());
        return WrapMapper.ok();
    }

    /**
     * 拼装简易协议
     *
     * @param messageVo 前端协议
     * @return 简易协议
     */
    private List<SimpleProtocolVo> assemble(MessageVo messageVo) {
        //判断是led 还是lcd 还是 照明
        List<SimpleProtocolVo> simpleProtocolVos = new ArrayList<>();
        Msg msg = JSON.parseObject(JSON.toJSONString(messageVo.getMsg()),Msg.class);
        //lcd设备
        lcdEquip(simpleProtocolVos, msg);
        //led设备
        ledEquip(simpleProtocolVos, msg);
        //智联照明
        lightIotcomm(simpleProtocolVos, msg,messageVo.getMsgcode());

        return simpleProtocolVos;
    }
    /**
     * 智联照明
     */
    private void lightIotcomm(List<SimpleProtocolVo> simpleProtocolVos, Msg msg,Integer msgcode) {
        //回路   集中控制器
        Optional.ofNullable(msg.getLoops()).ifPresent(loopLists -> {
            //根据did查询网管id
            loopLists.forEach(loops -> {
            SimpleProtocolVo simpleProtocolVo = new SimpleProtocolVo();
            //通过设备id查出回路和网关id
            //List<TController> controller = lightIotCommServiceApi.findController(s);
            //网关id
            //simpleProtocolVo.setDid(String.valueOf(controller.get(0)));
            HashMap<String, Object> data = new HashMap<>(3);
            //msgId
            simpleProtocolVo.setMsgid(UUID.randomUUID().toString());
            //判断msgcode掉那个设备接口
            if (msgcode == 220006) {
                TLoop tLoop = lightIotCommServiceApi.findLoopById(loops);
                TGateway tGateway = lightIotCommServiceApi.findgatewayById(tLoop.getGatewayId().intValue());
                //1-闭合；0-断开；2-读取回路
                log.info("接收到照明回路控制的命令 {} , {}", msg, new Date());
                //回路控制器
                //回路数组
                data.put(ConstantUtils.KYE_LOOPS, loops);
                //命令类型：1-闭合；0-断开；2-读取回路
                simpleProtocolVo.setDid(tGateway.getDid());
                switch (msg.getCmdtype()) {
                    //断开
                    case 0:
                        simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("light-iotcomm-10007").getMsgCode());
                        data.put(ConstantUtils.KYE_CCUID, tGateway.getCcuId());
                        data.put(ConstantUtils.KYE_BREAKERID, tLoop.getBreakerID());
                        data.put(ConstantUtils.KYE_SUBCMD, ConstantUtils.KYE_CLOSE);
                        simpleProtocolVo.setData(data);
                        break;
                    //闭合
                    case 1:
                        simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("light-iotcomm-10007").getMsgCode());
                        data.put(ConstantUtils.KYE_CCUID, tGateway.getCcuId());
                        data.put(ConstantUtils.KYE_BREAKERID,  tLoop.getBreakerID());
                        data.put(ConstantUtils.KYE_SUBCMD, ConstantUtils.KYE_OPEN);
                        simpleProtocolVo.setData(data);
                        break;
                    //读取回路  集中器回路开关列表查询
                    case 2:
                        simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("light-iotcomm-10009").getMsgCode());
                        data.put(ConstantUtils.KYE_CCUID,  tLoop.getBreakerID());
                        simpleProtocolVo.setData(data);
                        break;
                    default:
                }


            }
            if (msgcode == 220007 || msgcode == 220008) {
                //1-闭合；0-断开；2-读取回路；3-调光；4-重启设备 5-开灯；6-关灯
                if(msgcode == 220007 ){
                    log.info("接收到照明集中控制器的命令 {} , {}", msg, new Date());
                }else{
                    log.info("接收到照明点选集中控制器的命令 {} , {}", msg, new Date());

                }

                TLoop tLoop = lightIotCommServiceApi.findLoopById(loops);
                TGateway tGateway = lightIotCommServiceApi.findgatewayById(tLoop.getGatewayId().intValue());
                simpleProtocolVo.setDid(tGateway.getDid());
                switch (msg.getCmdtype()) {
                    //断开
                    case 0:
                        simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("light-iotcomm-10007").getMsgCode());
                        data.put(ConstantUtils.KYE_CCUID, tGateway.getCcuId());
                        data.put(ConstantUtils.KYE_BREAKERID, tLoop.getBreakerID());
                        data.put(ConstantUtils.KYE_SUBCMD, ConstantUtils.KYE_CLOSE);
                        simpleProtocolVo.setData(data);
                        break;
                    //闭合
                    case 1:
                        simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("light-iotcomm-10007").getMsgCode());
                        data.put(ConstantUtils.KYE_CCUID, tGateway.getCcuId());
                        data.put(ConstantUtils.KYE_BREAKERID,  tLoop.getBreakerID());
                        data.put(ConstantUtils.KYE_SUBCMD, ConstantUtils.KYE_OPEN);
                        simpleProtocolVo.setData(data);
                        break;
                    //读取回路
                    case 2:
                        simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("light-iotcomm-10009").getMsgCode());
                        data.put(ConstantUtils.KYE_CCUID,  tLoop.getBreakerID());
                        simpleProtocolVo.setData(data);
                        break;

                        //调光
                    case 3:
                        simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("light-iotcomm-10006").getMsgCode());
                        data.put(ConstantUtils.KYE_CCUID, tGateway.getCcuId());
                        data.put(ConstantUtils.KYE_SUBTYPE,  ConstantUtils.KYE_DIMMING);
                        data.put(ConstantUtils.KYE_LIGHTVALUE,msg.getLightValue());
                        simpleProtocolVo.setData(data);
                        break;
                    //4-重启设备
                    case 4:
                        simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("light-iotcomm-10005").getMsgCode());
                        data.put(ConstantUtils.KYE_CCUID, tGateway.getCcuId());
                        simpleProtocolVo.setData(data);
                        break;
                    // 5-开灯；
                    case 5:
                        simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("light-iotcomm-10006").getMsgCode());
                        data.put(ConstantUtils.KYE_CCUID, tGateway.getCcuId());
                        data.put(ConstantUtils.KYE_SUBTYPE,  ConstantUtils.KYE_ON);
                        data.put(ConstantUtils.KYE_LIGHTVALUE,100);
                        simpleProtocolVo.setData(data);
                        break;
                    //6-关灯
                    case 6:
                        simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("light-iotcomm-10006").getMsgCode());
                        data.put(ConstantUtils.KYE_CCUID, tGateway.getCcuId());
                        data.put(ConstantUtils.KYE_SUBTYPE,  ConstantUtils.KYE_OFF);
                        data.put(ConstantUtils.KYE_LIGHTVALUE,0);
                        simpleProtocolVo.setData(data);
                        break;
                    default:
                }

            }
            simpleProtocolVos.add(simpleProtocolVo);
        });
    });
        //单灯 单选灯杆
        Optional.ofNullable(msg.getLights()).ifPresent(lightLists -> {
            lightLists.forEach(lights -> {
                SimpleProtocolVo simpleProtocolVo = new SimpleProtocolVo();
                TController tController = lightIotCommServiceApi.findControllerById(lights);
                TGateway tGateway = lightIotCommServiceApi.findgatewayById(tController.getGatewayId().intValue());

                //网关id
                simpleProtocolVo.setDid(tGateway.getDid());
                HashMap<String, Object> data = new HashMap<>(3);
                //msgId
                simpleProtocolVo.setMsgid(UUID.randomUUID().toString());
                //判断msgcode掉那个设备接口
                if (msgcode == 220009 ||msgcode == 220010) {
                    if(msgcode == 220009 ){
                        //1-开；0-关；3-调光
                        log.info("接收到单灯控制的命令 {} , {}", msg, new Date());

                    }else{
                        log.info("接收到点选灯杆的命令 {} , {}", msg, new Date());

                    }

                    switch (msg.getCmdtype()) {
                        //关
                        case 0:
                            simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("light-iotcomm-10008").getMsgCode());
                            data.put(ConstantUtils.KYE_RTUID, tController.getRtuID());
                            data.put(ConstantUtils.KYE_SUBTYPE, ConstantUtils.KYE_OFF);
                            data.put(ConstantUtils.KYE_LIGHTVALUE, 0);
                            simpleProtocolVo.setData(data);
                            break;
                        //开
                        case 1:
                            simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("light-iotcomm-10008").getMsgCode());
                            data.put(ConstantUtils.KYE_RTUID, tGateway.getCcuId());
                            data.put(ConstantUtils.KYE_SUBTYPE, ConstantUtils.KYE_ON);
                            data.put(ConstantUtils.KYE_SUBCMD, 100);
                            simpleProtocolVo.setData(data);
                            break;
                        //调光
                        case 2:
                            simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("light-iotcomm-10008").getMsgCode());
                            data.put(ConstantUtils.KYE_RTUID, tGateway.getCcuId());
                            data.put(ConstantUtils.KYE_SUBTYPE,  ConstantUtils.KYE_DIMMING);
                            data.put(ConstantUtils.KYE_LIGHTVALUE,msg.getLightValue());
                            simpleProtocolVo.setData(data);
                            break;
                        default:
                    }
                }
                simpleProtocolVos.add(simpleProtocolVo);
            });
        });

    }
    /**
     * led设备封装简易协议 提取重复代码
     *
     * @param msg 报文数据
     */
    private void ledEquip(List<SimpleProtocolVo> simpleProtocolVos, Msg msg) {
        Optional.ofNullable(msg.getLeds()).ifPresent(leds -> {
            log.info("接收到led设备的命令 {} , {}", msg, new Date());
            //拼装数据
            leds.forEach(led -> {
                TLed tLed = ledClientService.findById(led);
                SimpleProtocolVo simpleProtocolVo = new SimpleProtocolVo();
                //设备编号
                simpleProtocolVo.setDid(tLed.getDid());
                //msgId
                simpleProtocolVo.setMsgid(UUID.randomUUID().toString());
                //亮度和音量 重启操作没有亮度和音量
                if (msg.getCmdtype() != 3) {
                    HashMap<String, Object> hashMap = new HashMap<>(1);

                    switch (msg.getCmdtype()) {
                        //开灯
                        case 1:
                            simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("led-tecnon-10002").getMsgCode());
                            hashMap.put("arg1", 1);
                            simpleProtocolVo.setData(hashMap);
                            break;
                        //关灯
                        case 2:
                            simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("led-tecnon-10002").getMsgCode());
                            hashMap.put("arg1", 0);
                            simpleProtocolVo.setData(hashMap);
                            break;
                        //调光
                        case 4:
                            simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("led-tecnon-10006").getMsgCode());
                            hashMap.put("arg1", msg.getLight());
                            simpleProtocolVo.setData(hashMap);
                            break;
                        //音量
                        case 5:
                            simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("led-tecnon-10004").getMsgCode());
                            hashMap.put("arg1", msg.getVolume());
                            simpleProtocolVo.setData(hashMap);
                            break;
                        default:
                    }
                } else {
                    //重启
                    simpleProtocolVo.setCmdid("10007");
                }
                simpleProtocolVos.add(simpleProtocolVo);
            });
        });
    }

    /**
     * lcd设备封装简易协议 提取重复代码
     *
     * @param msg 报文数据
     */
    private void lcdEquip(List<SimpleProtocolVo> simpleProtocolVos, Msg msg) {
        Optional.ofNullable(msg.getLcds()).ifPresent(lcds -> {
            log.info("接收到lcd设备的命令 {} , {}", msg, new Date());
            //拼装数据
            lcds.forEach(lcd -> {
                SimpleProtocolVo simpleProtocolVo = new SimpleProtocolVo();
                IotHobo iotHobo = lcdClientService.findById(lcd);
                //设备编号
                simpleProtocolVo.setDid(iotHobo.getDid());
                HashMap<String, Object> hashMap = new HashMap<>(1);
                hashMap.put("cids", lcd);
                simpleProtocolVo.setData(hashMap);
                //msgId
                simpleProtocolVo.setMsgid(UUID.randomUUID().toString());
                //lcd设备没有调光
                if (msg.getCmdtype() != 4) {
                    switch (msg.getCmdtype()) {
                        //开灯
                        case 1:
                            simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("lcd-hobovar-10003").getMsgCode());
                            break;
                        //关灯
                        case 2:
                            simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("lcd-hobovar-10004").getMsgCode());
                            break;
                        //重启
                        case 3:
                            simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("lcd-hobovar-10002").getMsgCode());
                            break;
                        //音量
                        case 5:
                            simpleProtocolVo.setCmdid(msgCodeService.findThingsMsgKey("lcd-hobovar-10006").getMsgCode());
                            hashMap.put("vol", msg.getVolume());
                            simpleProtocolVo.setData(hashMap);
                            break;
                        default:
                    }
                }
                simpleProtocolVos.add(simpleProtocolVo);
            });
        });
    }
}
