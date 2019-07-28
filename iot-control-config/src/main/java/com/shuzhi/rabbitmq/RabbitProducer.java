package com.shuzhi.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.shuzhi.entity.MqMessage;
import com.shuzhi.mapper.MqMessageMapper;
import com.shuzhi.websocket.socketvo.SimpleProtocolVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author zgk
 * @description 消息队列服务生产者
 * @date 2019-07-07 13:08
 */
@Slf4j
@Component
public class RabbitProducer {

    private  MqMessageMapper messageMapper;

    private  AmqpTemplate rabbitTemplate;

    private final StringRedisTemplate redisTemplate;

    public RabbitProducer(AmqpTemplate rabbitTemplate, MqMessageMapper messageMapper, StringRedisTemplate redisTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageMapper = messageMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 重试次数
     */
    @Value("${send.count}")
    private Integer count;

    /**
     * 超时时间 单位: 秒
     */
    @Value("${send.run-time}")
    private Integer runTime;


    static ReentrantLock lock = new ReentrantLock();

    static Hashtable<String,Condition> conditionHashtable = new Hashtable<>();

    /**
     * 向消息队列推送消息 如果没有回执则重试1次
     *
     * @param message 消息实体
     */
    public void sendMessage(SimpleProtocolVo message, Integer modulecode){

        //线程等待
        Condition condition = lock.newCondition();
        conditionHashtable.put(message.getMsgid(),condition);
        //查出Exchange 和topic
        MqMessage mqMessageSelect = new MqMessage();
        mqMessageSelect.setModulecode(modulecode);
        MqMessage mqMessage = messageMapper.selectOne(mqMessageSelect);
        //发送消息并等待
        lock.lock();
        messageWait(message, condition, mqMessage);
        //等待指定秒数 或被唤醒后查看map中有没有 没有则代表收到了回执结束方法 有责再发一次
        for (int i = 0; i < count; i++){
            if (conditionHashtable.get(message.getMsgid()) != null){
                //重新发送
                log.info("未接收到消息回执 msgId : {} 重试第 {} 次",message.getMsgid(),i+1);
                messageWait(message, condition, mqMessage);
            }else {
                //否则结束方法
                lock.unlock();
                return;
            }
        }
        log.info("获取回执消息失败 msgId : {}",message.getMsgid());
        //清除redis缓存
        redisTemplate.opsForHash().delete("web_socket_key", message.getMsgid());
        //清除map
        conditionHashtable.remove(message.getMsgid());
        lock.unlock();
    }

    private void messageWait(SimpleProtocolVo message, Condition condition, MqMessage mqMessage) {
        rabbitTemplate.convertAndSend(mqMessage.getExchange(), mqMessage.getTopic(), JSON.toJSONString(message));
        log.info("发送消息 : {} 线程 : {}", JSON.toJSONString(message), Thread.currentThread().getName());
        //线程等待
        try {
            condition.await(runTime, SECONDS);
            log.info("线程唤醒 {}", Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
            lock.unlock();
            log.error("服务器异常 : {}", e.getMessage());
        }
    }
}
