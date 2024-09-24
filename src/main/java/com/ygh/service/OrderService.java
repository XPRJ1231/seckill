package com.ygh.service;

import com.ygh.entity.OrderInfo;
import com.ygh.entity.SeckillOrder;
import com.ygh.entity.User;
import com.ygh.mapper.OrderMapper;
import com.ygh.redis.OrderKey;
import com.ygh.redis.RedisService;
import com.ygh.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final RedisService redisService;

    @Autowired
    public OrderService(OrderMapper orderMapper, RedisService redisService) {
        this.orderMapper = orderMapper;
        this.redisService = redisService;
    }

    public SeckillOrder getOrderByUserIdGoodsId(long userId, long goodsId) {
        return redisService.get(OrderKey.getSeckillOrderByUserIdGoodId, userId + "_" + goodsId, SeckillOrder.class);
        //"OrderKey:seckill18181818181_1"
    }

    public OrderInfo getOrderById(long orderId) {
        return orderMapper.getOrderById(orderId);
    }

    /**
     * 在订单详情表和秒杀订单表都新增一条数据<br>
     * 因为要同时新增，所以要保证两个操作是一个事物
     */
    @Transactional
    public OrderInfo createOrder(User user, GoodsVO goods) {

        OrderInfo orderInfo=OrderInfo.builder()
                .createDate(new Date())
                .deliveryAddrId(0L)
                .goodsCount(1)
                .goodsId(goods.getId())
                .goodsName(goods.getGoodsName())
                .goodsPrice(goods.getGoodsPrice())
                .orderChannel(1)
                .status(0)
                .userId(user.getId())
                .build();

        orderMapper.insert(orderInfo);

        SeckillOrder seckillOrder =SeckillOrder.builder()
                .goodsId(goods.getId())
                .orderId(orderInfo.getId())
                .userId(user.getId())
                .build();

        orderMapper.insertSeckillOrder(seckillOrder);

        redisService.set(OrderKey.getSeckillOrderByUserIdGoodId, user.getId() + "_" + goods.getId(), seckillOrder);

        return orderInfo;
    }


}
