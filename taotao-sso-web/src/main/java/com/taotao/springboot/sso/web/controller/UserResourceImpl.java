package com.taotao.springboot.sso.web.controller;

import com.alibaba.dubbo.config.annotation.Service;
import com.taotao.springboot.sso.common.utils.JacksonUtils;
import com.taotao.springboot.sso.domain.pojo.TbUser;
import com.taotao.springboot.sso.domain.result.TaotaoResult;
import com.taotao.springboot.sso.export.UserResource;
import com.taotao.springboot.sso.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * <p>Title: UserResourceImpl</p>
 * <p>Description: 用户管理Controller</p>
 * <p>Company: bupt.edu.cn</p>
 * <p>Created: 2018-05-06 11:00</p>
 * @author ChengTengfei
 * @version 1.0
 */
@Service(interfaceClass = UserResource.class)
@Controller
public class UserResourceImpl implements UserResource {

    private static final Logger log = LoggerFactory.getLogger(UserResourceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public TaotaoResult checkData(String data, int type) {
        TaotaoResult res = null;
        try {
            log.info("检查数据是否可用 checkData data = {}, type = {}", data, String.valueOf(type));
            res = userService.checkData(data, type);
            log.info("检查数据是否可用 checkData res = {}", JacksonUtils.objectToJson(res));
        } catch (Exception e){
            log.error("### Call UserResourceImpl.checkData error = {}", e);
        }
        return res;
    }

    @Override
    public TaotaoResult register(TbUser user) {
        TaotaoResult res = null;
        try {
            log.info("用户注册 register user = {}", JacksonUtils.objectToJson(user));
            res = userService.register(user);
            log.info("用户注册 register res = {}", JacksonUtils.objectToJson(res));
        } catch (Exception e){
            log.error("### Call UserResourceImpl.register error = {}", e);
        }
        return res;
    }

    @Override
    public TaotaoResult login(String username, String password) {
        TaotaoResult res = null;
        try {
            log.info("用户登录 login username = {}, password = {}", username, password);
            res = userService.login(username, password);
            log.info("用户登录 login res = {}", JacksonUtils.objectToJson(res));
        } catch (Exception e){
            log.error("### Call UserResourceImpl.login error = {}", e);
        }
        return res;
    }

    @Override
    public TaotaoResult getUserByToken(String token) {
        TaotaoResult res = null;
        try {
            log.info("根据token查询用户信息 getUserByToken token = {}", token);
            res = userService.getUserByToken(token);
            log.info("根据token查询用户信息 getUserByToken res = {}", JacksonUtils.objectToJson(res));
        } catch (Exception e){
            log.error("### Call UserResourceImpl.getUserByToken error = {}", e);
        }
        return res;
    }

    @Override
    public TaotaoResult logout(String token) {
        TaotaoResult res = null;
        try {
            log.info("用户退出 logout token = {}", token);
            res = userService.logout(token);
            log.info("用户退出 logout res = {}", JacksonUtils.objectToJson(res));
        } catch (Exception e){
            log.error("### Call UserResourceImpl.logout error = {}", e);
        }
        return res;
    }

}
