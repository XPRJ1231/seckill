package com.ygh.redis;

public class OrderKey extends BasePrefix {

    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getSeckillOrderByUserIdGoodId = new OrderKey("seckill");
}
