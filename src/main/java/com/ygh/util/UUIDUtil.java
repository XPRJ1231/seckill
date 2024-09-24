package com.ygh.util;

import java.util.UUID;

/**
 * 唯一id生成类
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
        //生成唯一标识符,randomUUID() 方法生成一个随机的UUID,格式如123e4567-e89b-12d3-a456-426614174000
    }

}
