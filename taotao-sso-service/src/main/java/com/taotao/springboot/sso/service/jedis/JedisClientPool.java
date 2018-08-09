package com.taotao.springboot.sso.service.jedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>Title: JedisClientPool</p>
 * <p>Description: </p>
 * <p>Company: bupt.edu.cn</p>
 * <p>Created: 2018-05-05 17:50</p>
 * @author ChengTengfei
 * @version 1.0
 */
@Component
public class JedisClientPool implements JedisClient{

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Boolean set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean expire(String key, int seconds) {
        try {
            if(seconds > 0){
                redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key, 1);
    }

    @Override
    public Boolean hset(String key, String field, String value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field).toString();
    }

    @Override
    public Long hdel(String key, String... field) {
        return redisTemplate.opsForHash().delete(key, field);
    }

}

