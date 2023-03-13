package com.sivan.security;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.sivan.common.exception.CaptchaException;
import com.sivan.common.lang.Const;
import com.sivan.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 图片验证码校验过滤器，在登录过滤器前
 */

// @Slf4j是用作日志输出的
@Slf4j
// @Component注解在一个类上,表示将此类标记为Spring容器中的一个Bean
@Component
// 验证码是一次性校验的，所以继承OncePerRequestFilter
public class CaptchaFilter extends OncePerRequestFilter {
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    // 注入redis工具类
    RedisUtil redisUtil;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    // 注入异常处理器
    LoginFailureHandler loginFailureHandler;

    // 重写方法
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // 获取url
        String url = httpServletRequest.getRequestURI();
        // 条件判断URL是否为Login请求
         if("/login".equals(url) && httpServletRequest.getMethod().equals("POST")){
            try{
                // 校验验证码
                validate(httpServletRequest);
            }catch (CaptchaException e){
                log.info(e.getMessage());
                // 异常处理————交给认证失败处理器
                loginFailureHandler.onAuthenticationFailure(httpServletRequest,httpServletResponse,e);
            }
        }
        // 过滤器链往下走
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
    //校验验证码逻辑
    private void validate(HttpServletRequest httpServletRequest) {
        // 获取用户传过来的code
        String code = httpServletRequest.getParameter("code");
        // 获取key
        String key  = httpServletRequest.getParameter("token");
        // 判断code和key是否为空
        if(StringUtils.isBlank(code) || StringUtils.isBlank(key)){
            // 抛出图片验证码异常
            throw new CaptchaException("验证码错误");
        }
        // 判断用户传来的code与redis中存储的code是否匹配
        if(!code.equals(redisUtil.hget(Const.CAPTCHA_KEY,key))){
            // 抛出图片验证码异常
            throw new CaptchaException("验证码错误");
        }
        // 一次性使用————验证码只能用一次
        redisUtil.hdel(Const.CAPTCHA_KEY,key);
    }
}
