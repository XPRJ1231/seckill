package com.ygh.rabbitmq;

import com.ygh.entity.SeckillOrder;
import com.ygh.entity.User;
import com.ygh.redis.RedisService;
import com.ygh.service.GoodsService;
import com.ygh.service.OrderService;
import com.ygh.service.SeckillService;
import com.ygh.vo.GoodsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消费者
 */
@Slf4j
@Service
public class MQReceiver {

    private final RedisService redisService;
    private final GoodsService goodsService;
    private final OrderService orderService;
    private final SeckillService seckillService;

    @Autowired
    public MQReceiver(RedisService redisService, GoodsService goodsService, OrderService orderService, SeckillService seckillService) {
        this.redisService = redisService;
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.seckillService = seckillService;
    }

    /**
     * 从队列中接收到消息,转换为SeckillMessage对象<br>
     * 提取user和goodsId<br>
     * 通过goodsId查询商品信息goodsVO<br>
     *
     * */
    @RabbitListener(queues = MQConfig.QUEUE) //监听队列的名称
    public void receive(String message) {

        log.info("receive message:{}", message);
        SeckillMessage m = RedisService.stringToBean(message, SeckillMessage.class);
        User user = m.getUser();
        long goodsId = m.getGoodsId();

        GoodsVO goodsVO = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVO.getStockCount();
        if (stock <= 0) {
            return;
        }

        //判断重复秒杀
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return;
        }

        //减库存 下订单 写入秒杀订单
        seckillService.seckill(user, goodsVO);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info(" topic queue1 message:{}", message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info(" topic queue2 message:{}", message);
    }
}
