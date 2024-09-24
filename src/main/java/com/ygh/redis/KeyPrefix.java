package com.ygh.redis;

/**
 * 缓冲key前缀
 */
public interface KeyPrefix {

    /**
     * 有效期
     */
    public int expireSeconds();

    /**
     * 前缀
     */
    public String getPrefix();
}
