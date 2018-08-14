package com.taotao.springboot.sso.service.jedis;

/**
 * <p>Title: JedisClient</p>
 * <p>Description: </p>
 * <p>Company: bupt.edu.cn</p>
 * <p>Created: 2018-05-05 17:48</p>
 * @author ChengTengfei
 * @version 1.0
 */
public interface JedisClient {

    /**
     * 普通缓存写入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    Boolean set(String key, String value);

    /**
     * 普通缓存读取
     * @param key 键
     * @return 值
     */
    String get(String key);

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    Boolean hasKey(String key);

    /**
     * 指定缓存失效时间
     * @param key 键
     * @param seconds 时间(秒)
     */
    Boolean expire(String key, int seconds);

    /**
     * 删除键
     * @param key 键
     * @return
     */
    Boolean delete(String key);

    /**
     * 判断键是否存在
     */
    Boolean exists(String key);


    /**
     * 设置key对应的value值自增1
     */
    Long incr(String key);

    /**
     *
     */
    Boolean hset(String key, String field, String value);

    /**
     *
     */
    String hget(String key, String field);

    /**
     *
     */
    Long hdel(String key, String... field);

}
