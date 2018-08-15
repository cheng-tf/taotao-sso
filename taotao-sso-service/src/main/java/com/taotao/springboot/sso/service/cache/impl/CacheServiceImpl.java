package com.taotao.springboot.sso.service.cache.impl;

import com.taotao.springboot.sso.service.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: CacheServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: bupt.edu.cn</p>
 * <p>Created: 2018-05-05 17:50</p>
 * @author ChengTengfei
 * @version 1.0
 */
@Service("CacheService")
public class CacheServiceImpl implements CacheService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    @Override
    public String randomKey() {
        return redisTemplate.randomKey();
    }

    @Override
    public Boolean expire(String key, int seconds) {
        if(seconds > 0){
            return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        }
        return false;
    }

    @Override
    public Long ttl(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public Boolean pexpire(String key, int milliseconds) {
        return redisTemplate.expire(key, milliseconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public Long pttl(String key) {
        return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }

    @Override
    public Boolean persist(String key) {
        return redisTemplate.persist(key);
    }

    @Override
    public Boolean set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public String get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    @Override
    public void mset(Map<String, String> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    @Override
    public List<String> mget(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    @Override
    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key, 1);
    }

    @Override
    public Boolean hset(String key, Object field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public String hget(String key, Object field) {
        return redisTemplate.opsForHash().get(key, field).toString();
    }

    @Override
    public List<Object> hmget(String key, Collection<Object> fields) {
        return redisTemplate.opsForHash().multiGet(key, fields);
    }

    @Override
    public Boolean hexists(String key, Object field) {
        try {
            redisTemplate.opsForHash().hasKey(key, field);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Long hdel(String key, Object... field) {
        return redisTemplate.opsForHash().delete(key, field);
    }

    @Override
    public Long hlen(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    @Override
    public Set<Object> hkeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    @Override
    public List<Object> hvals(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    @Override
    public Map<Object, Object> hgetall(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public Long rpush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

}

