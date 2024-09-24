package com.ygh.controller;

import com.ygh.entity.OrderInfo;
import com.ygh.entity.User;
import com.ygh.redis.RedisService;
import com.ygh.result.CodeMsg;
import com.ygh.result.Result;
import com.ygh.service.GoodsService;
import com.ygh.service.OrderService;
import com.ygh.service.UserService;
import com.ygh.vo.GoodsVO;
import com.ygh.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jiangyunxiong on 2018/5/28.
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVO> info(Model model, User user,
                                      @RequestParam("orderId") long orderId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVO goods = goodsService.getGoodsVOByGoodsId(goodsId);
        OrderDetailVO vo = new OrderDetailVO();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }

}
