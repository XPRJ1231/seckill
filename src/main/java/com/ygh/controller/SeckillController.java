package com.ygh.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.ygh.entity.SeckillOrder;
import com.ygh.entity.User;
import com.ygh.rabbitmq.MQSender;
import com.ygh.rabbitmq.SeckillMessage;
import com.ygh.redis.GoodsKey;
import com.ygh.redis.RedisService;
import com.ygh.result.CodeMsg;
import com.ygh.result.Result;
import com.ygh.service.GoodsService;
import com.ygh.service.OrderService;
import com.ygh.service.SeckillService;
import com.ygh.vo.GoodsVO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    private final GoodsService goodsService;
    private final OrderService orderService;
    private final SeckillService seckillService;
    private final RedisService redisService;
    private final MQSender sender;

    @Autowired
    public SeckillController(GoodsService goodsService, OrderService orderService, SeckillService seckillService, RedisService redisService, MQSender sender) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.seckillService = seckillService;
        this.redisService = redisService;
        this.sender = sender;
    }

    RateLimiter rateLimiter = RateLimiter.create(10);
    //基于令牌桶算法的限流实现类

    private final HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();
    //用来做标记，判断该商品是否被处理过了

    /**
     * 先使用RateLimiter实现限流<br>
     * 1.判断本地是否已标记，<br>
     * 2.预减库存，标记商品已处理
     * 3.
     * 将同步下单改为异步下单
     *
     * @param model g
     * @param user h
     * @param goodsId h
     * @return g
     */
    @RequestMapping(value = "/do_seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model, User user, @RequestParam("goodsId") long goodsId) {

        if (!rateLimiter.tryAcquire(1, TimeUnit.SECONDS)) { // tryAcquire方法：获取指定数量的许可（默认1个），并且在指定的时间内（1000毫秒）等待。如果在这个时间内成功获取到许可，方法返回 true；如果未能获取到许可，则返回 false。
            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED); // "访问高峰期，请稍等！"
        }

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR); // "Session不存在或者已经失效"
        }
        model.addAttribute("user", user);
        //将数据从控制器传递到视图，使得视图能够动态地展示这些数据。Spring MVC中实现数据传递和视图渲染的重要机制。

        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.SECKILL_OVER); // "商品已经秒杀完毕"
        }

        //TODO：双写不一致问题
        long stock = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);
        if (stock < 0) {
            afterPropertiesSet(); // 如果redis显示库存不足，重新初始化商品库存（从数据库中加载商品的库存信息）。
            long stock2 = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);
            if (stock2 < 0) {
                localOverMap.put(goodsId, true); // 标记该商品已经处理
                return Result.error(CodeMsg.SECKILL_OVER); // "商品已经秒杀完毕"
            }
        }

        //判断重复秒杀
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEAT_SECKILL); // "不能重复秒杀"
        }

        //入队，异步下单
        SeckillMessage message = new SeckillMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        sender.sendSeckillMessage(message);
        return Result.success(0);//排队中
    }

    /**
     * 系统初始化,将商品信息加载到redis和本地内存
     */
    @Override
    public void afterPropertiesSet() {
        List<GoodsVO> goodsVOList = goodsService.listGoodsVO();
        if (goodsVOList == null) {
            return;
        }
        for (GoodsVO goods : goodsVOList) {
            redisService.set(GoodsKey.getGoodsStock, "" + goods.getId(), goods.getStockCount());
            //初始化商品都是没有处理过的
            localOverMap.put(goods.getId(), false);
        }
    }

    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, User user,
                                      @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long orderId = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(orderId);
    }
}
