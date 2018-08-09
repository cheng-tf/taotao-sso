package com.taotao.springboot.sso.web;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <p>Title: SpringbootApplication</p>
 * <p>Description: </p>
 * <p>Company: bupt.edu.cn</p>
 * <p>Created: 2018-05-11 23:59</p>
 * @author ChengTengfei
 * @version 1.0
 */
@SpringBootApplication
@MapperScan(basePackages = "com.taotao.springboot.sso.mapper")
@ComponentScan(basePackages = "com.taotao.springboot.sso.*")
@EnableTransactionManagement                                        // 启动注解事务管理
@EnableDubboConfiguration                                           // 启动Dubbo配置
public class SpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

}
