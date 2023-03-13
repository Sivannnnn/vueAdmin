package com.sivan.security;

import cn.hutool.json.JSONUtil;
import com.sivan.common.lang.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 配置登录失败处理器
 */

// @Component注解在一个类上,表示将此类标记为Spring容器中的一个Bean
@Component
//重写方法————返回JSON数据
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 返回数据设置成JSON格式
        response.setContentType("application/json;charset=UTF-8");
        // 写入流
        ServletOutputStream outputStream = response.getOutputStream();
        // 获取异常信息
        Result result = Result.fail(
           "Bad credentials".equals(exception.getMessage()) ? "用户名或密码不正确" : exception.getMessage()
        );
        // 将结果转换成JSON格式，变成字节码的形式
        outputStream.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        // 推出去
        outputStream.flush();
        // 关闭
        outputStream.close();
    }
}