package com.taotao.springboot.sso.common.utils;

import java.util.Random;

/**
 * <p>Title: IDUtils</p>
 * <p>Description: ID生成策略工具类</p>
 * <p>Company: bupt.edu.cn</p>
 * <p>Created: 2018-05-05 16:32</p>
 * @author ChengTengfei
 * @version 1.0
 */
public class IDUtils {

    /**
     * 生成图片名
     */
    public static String genImageName() {
        // #1 获取当前时间的长整形值（毫秒）
        // long millis = System.nanoTime();
        long millis = System.currentTimeMillis();
        // #2 三位随机数
        int end3 = new Random().nextInt(999);
        // #3 拼接，且不足三位则补零
        return millis + String.format("%03d", end3);
    }

    /**
     * 生成商品ID
     */
    public static long genItemId() {
        // #1 获取当前时间的长整形值（毫秒）
        // long millis = System.nanoTime();
        long millis = System.currentTimeMillis();
        // #2 两位随机数
        int end2 = new Random().nextInt(99);
        // #3 拼接，且不足两位则补零
        return new Long(millis + String.format("%02d", end2));
    }

}