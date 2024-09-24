package com.ygh.rabbitmq;

import com.ygh.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 生产者
 */
@Slf4j
@Service
public class MQSender {

    private final AmqpTemplate amqpTemplate;

    @Autowired
    public MQSender(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    /**
     * 将消息发送到指定的交换机和路由键
     */
    public void sendTopic(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send topic message:{}", msg);

        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
        //交换机、路由键（用于路由消息的键，决定消息的去向）、消息内容
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
    }

    public void sendSeckillMessage(SeckillMessage message) {
        String msg = RedisService.beanToString(message);
        log.info("send message:{}", msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);

    }
}
