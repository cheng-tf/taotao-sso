package com.taotao.springboot.sso.service;

import com.taotao.springboot.sso.domain.pojo.TbUser;
import com.taotao.springboot.sso.domain.result.TaotaoResult;

/**
 * <p>Title: UserService</p>
 * <p>Description: 用户管理Service</p>
 * <p>Company: bupt.edu.cn</p>
 * <p>Created: 2018-05-06 10:48</p>
 * @author ChengTengfei
 * @version 1.0
 */
public interface UserService {

    /**
     * 检查数据是否可用
     */
    TaotaoResult checkData(String data, int type);

    /**
     * 用户注册
     */
    TaotaoResult register(TbUser user);

    /**
     * 用户登录
     */
    TaotaoResult login(String username, String password);

    /**
     * 根据token查询用户信息
     */
    TaotaoResult getUserByToken(String token);

    /**
     * 用户退出
     */
    TaotaoResult logout(String token);


}

