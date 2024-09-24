package com.ygh.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置bean
 */
@Configuration
public class MQConfig {

    public static final String SECKILL_QUEUE = "seckill.queue";
    public static final String QUEUE = "queue";
    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String TOPIC_EXCHANGE = "topicExchange";


    /**
     * 队列名称、是否持久化
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE1, true);
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE2, true);
    }

    /**
     * 主题交换机（通配符交换机）<br>
     * 通配符："*"、"#"
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    /**
     * 绑定交换机和队列，并设置匹配键
     */
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder
                .bind(topicQueue1())
                .to(topicExchange())
                .with("topic.key1");
    }

    @Bean
    public Binding topicBinding2() {
        return BindingBuilder
                .bind(topicQueue2())
                .to(topicExchange())
                .with("topic.#");
    }


}
