package com.sivan.security;

import cn.hutool.json.JSONUtil;
import com.sivan.common.lang.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// @Component注解在一个类上,表示将此类标记为Spring容器中的一个Bean
@Component
public class JwtAcessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 返回数据设置成JSON格式
        response.setContentType("application/json;charset=UTF-8");
        // 设置状态码
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // 写入流
        ServletOutputStream outputStream = response.getOutputStream();
        // 获取异常信息
        Result result = Result.fail(accessDeniedException.getMessage());
        // 将结果转换成JSON格式，变成字节码的形式
        outputStream.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        // 推出去
        outputStream.flush();
        // 关闭
        outputStream.close();
    }
}
