package com.taotao.springboot.sso.service.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Title: CacheService</p>
 * <p>Description: </p>
 * <p>Company: bupt.edu.cn</p>
 * <p>Created: 2018-05-05 17:48</p>
 * @author ChengTengfei
 * @version 1.0
 */
public interface CacheService {

    //*******************************************键管理*****************************************************************

    /**
     * 判断键是否存在
     */
    Boolean exists(String key);

    /**
     * 删除键
     */
    Boolean del(String key);

    /**
     * 键重命名
     */
    void rename(String oldKey, String newKey);

    /**
     * 随机返回一个键
     */
    String randomKey();

    /**
     * 设置键过期时间（秒）
     */
    Boolean expire(String key, int seconds);

    /**
     * 获取剩余过期时间（秒）
     */
    Long ttl(String key);

    /**
     * 设置键过期时间（毫秒）
     */
    Boolean pexpire(String key, int milloseconds);

    /**
     * 获取剩余过期时间（毫秒）
     */
    Long pttl(String key);

    /**
     * 清除键过期时间
     */
    Boolean persist(String key);

    //*******************************************字符串*****************************************************************

    /**
     * 设置值
     */
    Boolean set(String key, String value);

    /**
     * 获取值
     */
    String get(String key);

    /**
     * 批量设置值
     */
    void mset(Map<String, String> map);

    /**
     * 批量获取值
     */
    List<String> mget(Collection<String> keys);

    /**
     * 计数自增
     */
    Long incr(String key);

    //*******************************************哈希*****************************************************************

    /**
     * 设置值
     */
    Boolean hset(String key, Object field, Object value);

    /**
     * 获取值
     */
    String hget(String key, Object field);

    /**
     * 批量获取值
     */
    List<Object> hmget(String key, Collection<Object> fields);

    /**
     * 判断field字段是否存在
     */
    Boolean hexists(String key, Object field);

    /**
     * 删除field
     */
    Long hdel(String key, Object... field);

    /**
     * 计算field个数
     */
    Long hlen(String key);

    /**
     * 获取所有field
     */
    Set<Object> hkeys(String key);

    /**
     * 获取所有value
     */
    List<Object> hvals(String key);

    /**
     * 获取所有的field-value
     */
    Map<Object, Object> hgetall(String key);

    //*******************************************列表*****************************************************************

    /**
     * 从右边插入元素
     */
    Long rpush(String key, String value);


    // 未完待续









}
