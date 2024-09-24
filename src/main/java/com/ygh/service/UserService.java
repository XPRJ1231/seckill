package com.ygh.service;

import com.alibaba.druid.util.StringUtils;
import com.ygh.entity.User;
import com.ygh.exception.GlobalException;
import com.ygh.mapper.UserMapper;
import com.ygh.redis.RedisService;
import com.ygh.redis.UserKey;
import com.ygh.result.CodeMsg;
import com.ygh.util.MD5Util;
import com.ygh.util.UUIDUtil;
import com.ygh.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserService {

    private UserMapper userMapper;
    private RedisService redisService;

    @Autowired
    public void setUserMapper(UserMapper userMapper, RedisService redisService) {
        this.userMapper = userMapper;
        this.redisService = redisService;
    }


    public static final String COOKIE_NAME_TOKEN = "token";

    /**
     * 缓存中查询，缓存中没有再去数据库中查询并存入缓存
     */
    public User getById(long id) {

        User user = redisService.get(UserKey.getById, "" + id, User.class);
        if (user != null) {
            return user;
        }

        user = userMapper.getById(id);

        if (user != null) {
            redisService.set(UserKey.getById, "" + id, user);
        }
        return user;
    }

    /**
     * 典型缓存同步场景：更新密码<p>
     * 根据id查询user<br>
     * 更新数据库<br>
     * 更新缓存（删除、插入）
     */
    public boolean updatePassword(String token, long id, String formPass) {

        User user = getById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        User toBeUpdate = new User();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        userMapper.update(toBeUpdate);

        redisService.delete(UserKey.getById, "" + id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(UserKey.token, token, user);
        return true;
    }

    /**
     * 1.loginVO为空：服务端异常<br>
     * 2.根据手机号查询user为空：手机号不存在<br>
     * 3.验证密码加密后和存储的密码是否一致<br>
     *
     */
    public String login(HttpServletResponse response, LoginVO loginVO) {
        if (loginVO == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVO.getMobile();
        String formPass = loginVO.getPassword();

        User user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //生成唯一id作为token
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return token;
    }

    /**
     * 将token做为key，用户信息做为value 存入redis模拟session
     * 同时将token存入cookie，保存登录状态
     */
    public void addCookie(HttpServletResponse response, String token, User user) {
        redisService.set(UserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setPath("/");//设置为网站根目录
        response.addCookie(cookie);
    }

    /**
     * 根据token获取用户信息
     */
    public User getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        User user = redisService.get(UserKey.token, token, User.class);
        //延长有效期，有效期等于最后一次操作+有效期
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }

}
