package com.sivan.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 配置图片验证码异常类
 */

//继承身份异常类
public class CaptchaException extends AuthenticationException {
    // 重写方法
    public CaptchaException(String msg) {
        super(msg);
    }
}
