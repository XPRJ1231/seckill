package com.ygh.vo;

import com.ygh.entity.OrderInfo;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderDetailVO {
    private GoodsVO goods;
    private OrderInfo order;

}
