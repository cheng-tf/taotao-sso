package com.taotao.springboot.sso.service.impl;

import com.taotao.springboot.sso.common.utils.JacksonUtils;
import com.taotao.springboot.sso.domain.pojo.TbUser;
import com.taotao.springboot.sso.domain.pojo.TbUserExample;
import com.taotao.springboot.sso.domain.result.TaotaoResult;
import com.taotao.springboot.sso.mapper.TbUserMapper;
import com.taotao.springboot.sso.service.UserService;
import com.taotao.springboot.sso.service.cache.CacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>Title: UserServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: bupt.edu.cn</p>
 * <p>Created: 2018-05-06 10:50</p>
 * @author ChengTengfei
 * @version 1.0
 */
@Service
@Transactional(propagation= Propagation.REQUIRED, isolation= Isolation.DEFAULT)
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private CacheService cacheService;

    @Value("${USER_SESSION}")
    private String USER_SESSION;

    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public TaotaoResult checkData(String data, int type) {
        // #1 设置查询条件
        TbUserExample userExample = new TbUserExample();
        TbUserExample.Criteria criteria = userExample.createCriteria();
        if (type == 1) {			// 判断用户名是否可用
            criteria.andUsernameEqualTo(data);
        } else if(type == 2) {		// 判断手机号是否可用
            criteria.andPhoneEqualTo(data);
        } else if(type == 3) {		// 判断邮箱是否可用
            criteria.andEmailEqualTo(data);
        } else {
            return TaotaoResult.build(400, "参数中包含非法数据");
        }
        // #2 执行查询
        List<TbUser> list = userMapper.selectByExample(userExample);
        if (list != null && list.size() > 0) {
            return TaotaoResult.ok(false);
        }
        // #3 若查询结果为空，则数据可用
        return TaotaoResult.ok(true);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public TaotaoResult register(TbUser user) {
        // #1 检查数据的有效性
        // #1.1 判断用户名
        if (StringUtils.isBlank(user.getUsername())) {
            return TaotaoResult.build(400, "用户名不能为空");
        }
        TaotaoResult taotaoResult = checkData(user.getUsername(), 1);
        if (! (Boolean)taotaoResult.getData()) {
            return TaotaoResult.build(400, "用户名重复");
        }
        // #1.2 判断密码
        if (StringUtils.isBlank(user.getPassword())) {
            return TaotaoResult.build(400, "密码不能为空");
        }
        // #1.3 判断手机号
        if (StringUtils.isNoneBlank(user.getPhone())) {
            taotaoResult = checkData(user.getPhone(), 2);
            if (! (Boolean)taotaoResult.getData()) {
                return TaotaoResult.build(400, "电话号码重复");
            }
        }
        // #1.4 判断邮箱
        if (StringUtils.isNoneBlank(user.getEmail())) {
            taotaoResult = checkData(user.getEmail(), 3);
            if (! (Boolean)taotaoResult.getData()) {
                return TaotaoResult.build(400, "email重复");
            }
        }
        // #2 补全POJO的属性
        user.setCreated(new Date());
        user.setUpdated(new Date());
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));// 密码MD5加密
        // #3 插入
        userMapper.insert(user);
        return TaotaoResult.ok();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public TaotaoResult login(String username, String password) {
        // #1 判断用户名密码是否正确
        TbUserExample userExample = new TbUserExample();
        TbUserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        // #1.1 校验用户名
        List<TbUser> list = userMapper.selectByExample(userExample);
        if (list == null || list.size() == 0) {
            return TaotaoResult.build(400, "用户名或密码不正确");
        }
        // #1.2 校验密码
        TbUser user = list.get(0);
        if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
            return TaotaoResult.build(400, "用户名或密码不正确");
        }
        // #2 登录成功后，使用UUID生成TOKEN令牌，相当于JSESSIONID
        String token = UUID.randomUUID().toString();
        user.setPassword(null);
        // #3 将用户信息保存到Redis缓存：key为TOKEN令牌，value为JSON序列化数据
        cacheService.set(USER_SESSION + ":" + token, JacksonUtils.objectToJson(user));
        // #4 设置键过期时间，用于模拟Session过期时间
        cacheService.expire(USER_SESSION + ":" + token, SESSION_EXPIRE);
        // #5 封装TOKEN令牌返回，以放入Cookie保存
        return TaotaoResult.ok(token);
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public TaotaoResult getUserByToken(String token) {
        // #1 根据TOKEN令牌查询缓存
        String json = cacheService.get(USER_SESSION + ":" + token);
        if (StringUtils.isBlank(json)) {
            // 用户登录已经过期
            return TaotaoResult.build(400, "用户登录已经过期");
        }
        // #2 若查询命中，则用户已登录，重置键的过期时间。
        cacheService.expire(USER_SESSION + ":" + token, SESSION_EXPIRE);
        // #3 JSON反序列化，封装返回
        return TaotaoResult.ok(JacksonUtils.jsonToPojo(json, TbUser.class));
    }


    @Override
    public TaotaoResult logout(String token) {
        // #1 删除TOKEN令牌
        if(token != null) {
            cacheService.del(USER_SESSION + ":" + token);
        }
        return TaotaoResult.ok();
    }

}