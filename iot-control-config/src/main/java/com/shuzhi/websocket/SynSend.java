package com.shuzhi.websocket;

import com.shuzhi.rabbitmq.RabbitProducer;
import com.shuzhi.websocket.socketvo.SimpleProtocolVo;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author zgk
 * @description 异步推送消息
 * @date 2019-07-12 13:49
 */
public class SynSend extends Thread {

    private RabbitProducer rabbitProducer;

    private SimpleProtocolVo message;

    private Integer modulecode;

    private StringRedisTemplate redisTemplate;

    SynSend(RabbitProducer rabbitProducer, SimpleProtocolVo message, Integer modulecode, StringRedisTemplate redisTemplate) {
        this.rabbitProducer = rabbitProducer;
        this.message = message;
        this.modulecode = modulecode;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run() {
        super.run();
        redisTemplate.opsForHash().put("web_socket_key", message.getMsgid(), String.valueOf(modulecode));
        rabbitProducer.sendMessage(message,modulecode);
    }
}
