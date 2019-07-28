package com.shuzhi.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.shuzhi.entity.MqMessage;
import com.shuzhi.service.MqMessageService;
import com.shuzhi.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.locks.Condition;

/**
 * @author zgk
 * @description 消息队列服务消费者
 * @date 2019-07-07 13:08
 */
@Slf4j
@Component
public class RabbitConsumer {

    private final WebSocketServer webSocketServer;

    private final StringRedisTemplate redisTemplate;

    private final MqMessageService mqMessageService;

    public RabbitConsumer(WebSocketServer webSocketServer, StringRedisTemplate redisTemplate, MqMessageService mqMessageService) {
        this.webSocketServer = webSocketServer;
        this.redisTemplate = redisTemplate;
        this.mqMessageService = mqMessageService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "equip-up", durable = "true"),
            exchange = @Exchange(value = "equip", durable = "true", type = "topic")
    ))
    @RabbitHandler
    public void consumer(@Payload String message, @Headers Map<String, Object> headers,
                         Channel channel) throws IOException, ParseException {

        log.info("--------------收到消息，开始消费------------");
        log.info("消息是 : {}", message);

        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        Message message1 = JSON.parseObject(message, Message.class);
        message1.setMsgtype("2");
        //推送消息
        String key = null;
        try {
            key = (String) redisTemplate.opsForHash().get("web_socket_key", message1.getMsgid());
        }catch (Exception e){
            log.warn("消息类型错误 : {}",message);
        }
        if (StringUtils.isNotBlank(key)){
            //判断要调用哪个方法 并调用
            isEquip(key);
            //唤醒线程
            Condition condition = RabbitProducer.conditionHashtable.get(message1.getMsgid());
            if (condition != null){
                RabbitProducer.lock.lock();
                condition.signalAll();
                RabbitProducer.conditionHashtable.remove(message1.getMsgid());
                RabbitProducer.lock.unlock();
                log.info("唤醒线程 msgId : {}",message1.getMsgid());
            }
            //删除map中的数据
        }else {
            log.warn("msgId不存在 : {}",message1.getMsgid());
        }
        // ACK
        channel.basicAck(deliveryTag, false);
    }

    /**
     * 判断是哪个设备发来的的消息  并触发对应的定时任务
     *
     * @param key modulecode
     */
    private void isEquip(String key) throws ParseException {

        MqMessage mqMessageSelect = new MqMessage();
        mqMessageSelect.setModulecode(Integer.valueOf(key));
        MqMessage mqMessage = mqMessageService.selectOne(mqMessageSelect);

        switch (mqMessage.getExchange()){
            case "lcd" :
                //调用lcd设备的信息
                webSocketServer.lcd();
                break;
            case "led" :
                webSocketServer.led();
                break;
            case "light" :
                webSocketServer.light();
                break;
            default:
        }

    }
}
