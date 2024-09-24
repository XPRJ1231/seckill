package com.ygh.rabbitmq;

import com.ygh.entity.User;
import lombok.Getter;
import lombok.Setter;

/**
 * 消息体（user、goodsId）
 */
@Setter
@Getter
public class SeckillMessage {

    private User user;
    private long goodsId;

}
