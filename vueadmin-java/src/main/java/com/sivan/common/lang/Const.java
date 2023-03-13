package com.sivan.common.lang;

/**
 * 生成常量key的类，便于存入redis
 */

public class Const {

    public final static String CAPTCHA_KEY = "capcha";

    // 设置用户状态 0——角色正常 1——不正常，也就是禁用状态
    public final static Integer STATUS_ON = 0;
    public final static Integer STATUS_OFF = 1;


    public static final String DEFAULT_PASSWORD = "888888";
public static final String DEFALUT_AVATAR = "https://tuchuangs.com/imgs/2022/10/25/38d3952f6aabeb88.jpg";
}
