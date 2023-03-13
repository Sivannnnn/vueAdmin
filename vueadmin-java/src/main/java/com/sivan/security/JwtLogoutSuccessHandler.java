package com.sivan.security;

import cn.hutool.json.JSONUtil;
import com.sivan.common.lang.Result;
import com.sivan.utils.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登出账号处理
 */

// @Component注解在一个类上,表示将此类标记为Spring容器中的一个Bean
@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    // 引入jwt
    JwtUtils jwtUtils;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //判断认证身份凭证是否为空
        if(authentication != null){
            // 手动退出
            new SecurityContextLogoutHandler().logout(request,response,authentication);
        }
        // 返回数据设置成JSON格式
        response.setContentType("application/json;charset=UTF-8");
        // 写入流
        ServletOutputStream outputStream = response.getOutputStream();
        // 清空jwt，返回空字符串
        response.setHeader(jwtUtils.getHeader(),"");
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
