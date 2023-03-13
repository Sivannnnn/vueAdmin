package com.sivan.security;

import cn.hutool.json.JSONUtil;
import com.sivan.common.lang.Result;
import com.sivan.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 配置登录成功处理器
 */


// @Component注解在一个类上,表示将此类标记为Spring容器中的一个Bean
@Component
//重写方法————返回JSON数据
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    // 引入jwt
    JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 返回数据设置成JSON格式
        response.setContentType("application/json;charset=UTF-8");
        // 写入流
        ServletOutputStream outputStream = response.getOutputStream();
        // 生成jwt，并放置到请求头中
        String jwt = jwtUtils.generateToken(authentication.getName());
        response.setHeader(jwtUtils.getHeader(),jwt);
        //
        Result result = Result.succ("");
        // 将结果转换成JSON格式，变成字节码的形式
        outputStream.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        // 推出去
        outputStream.flush();
        // 关闭
        outputStream.close();
    }
}