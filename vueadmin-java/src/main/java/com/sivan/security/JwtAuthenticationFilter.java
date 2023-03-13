package com.sivan.security;

import cn.hutool.core.util.StrUtil;
import com.sivan.entity.SysUser;
import com.sivan.service.SysUserService;
import com.sivan.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 识别Jwt用户凭证和username，实现自动登录功能
 */


public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    JwtUtils jwtUtils;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    UserDetailServiceImpl userDetailService;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    SysUserService sysUserService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    // 重写过滤方法
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 获取Jwt
        String jwt = request.getHeader(jwtUtils.getHeader());
        // 判断Jwt是否为空
        if(StrUtil.isBlankOrUndefined(jwt)){
            // 过滤器链往下走
            chain.doFilter(request,response);
            return;
        }

        // 解析jwt
        Claims claim = jwtUtils.getClaimByToken(jwt);
        // 判断是否为空
        if(claim == null){
            // 若为空，则该数据为非法，抛出异常
            throw new JwtException("token 异常");
        }
        // 判断token是否过期
        if(jwtUtils.isTokenExpired(claim)){
            throw new JwtException("token 已过期");
        }

        // 获取用户
        String username = claim.getSubject();
        // 获取用户的权限等信息
        SysUser sysUser = sysUserService.getByUsername(username);
        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(username,null,userDetailService.getUserAuthority(sysUser.getId()));
        // 设置认证主体
        SecurityContextHolder.getContext().setAuthentication(token);
        // 登陆完成后，过滤器链继续往下走
        chain.doFilter(request,response);
    }
}
