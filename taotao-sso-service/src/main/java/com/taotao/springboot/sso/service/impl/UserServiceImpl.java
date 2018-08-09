package com.taotao.springboot.sso.service.impl;

import com.taotao.springboot.sso.common.utils.JacksonUtils;
import com.taotao.springboot.sso.domain.pojo.TbUser;
import com.taotao.springboot.sso.domain.pojo.TbUserExample;
import com.taotao.springboot.sso.domain.result.TaotaoResult;
import com.taotao.springboot.sso.mapper.TbUserMapper;
import com.taotao.springboot.sso.service.UserService;
import com.taotao.springboot.sso.service.jedis.JedisClient;
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
    private JedisClient jedisClient;
    @Value("${USER_SESSION}")
    private String USER_SESSION;
    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;


    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public TaotaoResult checkData(String data, int type) {
        TbUserExample userExample = new TbUserExample();
        TbUserExample.Criteria criteria = userExample.createCriteria();
        // 设置查询条件
        if (type == 1) {			// #1 判断用户名是否可用
            criteria.andUsernameEqualTo(data);
        } else if(type == 2) {		// #2 判断手机号是否可用
            criteria.andPhoneEqualTo(data);
        } else if(type == 3) {		// #3 判断邮箱是否可用
            criteria.andEmailEqualTo(data);
        } else {
            return TaotaoResult.build(400, "参数中包含非法数据");
        }
        // 执行查询
        List<TbUser> list = userMapper.selectByExample(userExample);
        if (list != null && list.size() > 0) {
            // 查询到数据，返回false
            return TaotaoResult.ok(false);
        }
        // 数据可使用
        return TaotaoResult.ok(true);
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public TaotaoResult register(TbUser user) {
        // 检查数据的有效性
        // 判断用户名
        if (StringUtils.isBlank(user.getUsername())) {
            return TaotaoResult.build(400, "用户名不能为空");
        }
        TaotaoResult taotaoResult = checkData(user.getUsername(), 1);
        if (! (Boolean)taotaoResult.getData()) {
            return TaotaoResult.build(400, "用户名重复");
        }
        // 判断密码
        if (StringUtils.isBlank(user.getPassword())) {
            return TaotaoResult.build(400, "密码不能为空");
        }
        // 判断手机号
        if (StringUtils.isNoneBlank(user.getPhone())) {
            taotaoResult = checkData(user.getPhone(), 2);
            if (! (Boolean)taotaoResult.getData()) {
                return TaotaoResult.build(400, "电话号码重复");
            }
        }
        // 判断邮箱
        if (StringUtils.isNoneBlank(user.getEmail())) {
            taotaoResult = checkData(user.getEmail(), 3);
            if (! (Boolean)taotaoResult.getData()) {
                return TaotaoResult.build(400, "email重复");
            }
        }
        // 补全POJO的属性
        user.setCreated(new Date());
        user.setUpdated(new Date());
        // 密码进行MD5加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        // 插入数据
        userMapper.insert(user);
        // 返回
        return TaotaoResult.ok();
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public TaotaoResult login(String username, String password) {
        // 1、判断用户名密码是否正确。
        TbUserExample userExample = new TbUserExample();
        TbUserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        // 校验用户名
        List<TbUser> list = userMapper.selectByExample(userExample);
        if (list == null || list.size() == 0) {
            return TaotaoResult.build(400, "用户名或密码不正确");
        }
        // 校验密码
        TbUser user = list.get(0);
        if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
            return TaotaoResult.build(400, "用户名或密码不正确");
        }
        // 2、登录成功后，使用UUID生成token，相当于Jsessionid
        String token = UUID.randomUUID().toString();
        user.setPassword(null);
        // 3、将用户信息保存到Redis；Key为token，value为TbUser对象转换成JSON后的数据
        jedisClient.set(USER_SESSION + ":" + token, JacksonUtils.objectToJson(user));
        // 4、设置key的过期时间，用于模拟Session的过期时间
        jedisClient.expire(USER_SESSION + ":" + token, SESSION_EXPIRE);
        // 5、返回TaotaoResult，且包装token
        return TaotaoResult.ok(token);
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public TaotaoResult getUserByToken(String token) {
        // 1、根据token查询Redis
        String json = jedisClient.get(USER_SESSION + ":" + token);
        if (StringUtils.isBlank(json)) {
            return TaotaoResult.build(400, "用户登录已经过期");		// 返回用户已经过期
        }
        // 2、若查询到数据，则说明用户已经登录，且重置key的过期时间。
        jedisClient.expire(USER_SESSION + ":" + token, SESSION_EXPIRE);
        // 3、把JSON数据转换成TbUser对象，再使用TaotaoResult包装并返回。
        return TaotaoResult.ok(JacksonUtils.jsonToPojo(json, TbUser.class));
    }


    @Override
    public TaotaoResult logout(String token) {
        return TaotaoResult.ok();
    }


}
