package com.ygh.service;

import com.ygh.entity.OrderInfo;
import com.ygh.entity.SeckillOrder;
import com.ygh.entity.User;
import com.ygh.redis.RedisService;
import com.ygh.redis.SeckillKey;
import com.ygh.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillService {

    private final GoodsService goodsService;
    private final OrderService orderService;
    private final RedisService redisService;

    @Autowired
    public SeckillService(GoodsService goodsService, OrderService orderService, RedisService redisService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.redisService = redisService;
    }

    /**
     * 秒杀<p>
     * 减库存 下订单 写入秒杀订单<br>
     * 保证这三个操作，是一个事务
     */
    @Transactional
    public OrderInfo seckill(User user, GoodsVO goods) {
        //减库存
        boolean success = goodsService.reduceStock(goods);
        if (success) {
            //下订单 写入秒杀订单
            return orderService.createOrder(user, goods);
        } else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    /**
     * 获取秒杀结果<br>
     * order不为空，成功<br>
     * 为空则判断秒杀状态<br>
     * -1：秒杀失败；0：秒杀进行中
     */
    public long getSeckillResult(long userId, long goodsId) {
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(userId, goodsId);
        if (order != null) {
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1; // 秒杀失败
            } else {
                return 0; // 秒杀进行中
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, "" + goodsId);
    }
}
