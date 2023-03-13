package com.sivan.utils;

import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT：用户凭证————访问其他请求所携带
 */

// @Data注解的主要作用是提高代码的简洁，使用这个注解可以省去实体类中大量的get()、 set()、 toString()等方法。
@Data
// @Component注解在一个类上,表示将此类标记为Spring容器中的一个Bean
@Component
// @ConfigurationProperties注解用于自动配置绑定，可以将application.properties配置中的值注入到bean对象上。
@ConfigurationProperties(prefix = "sivan.jwt")
public class JwtUtils {
    // 自定义参数
    private long expire;
    // 密钥
    private String secret;
    // 给jwt起名为header
    private String header;

    // 生成jwt
    public String generateToken(String username){
        // 获取当前时间
        Date nowDate = new Date();
        // 配置过期时间
        Date expireDate = new Date(nowDate.getTime() + 1000 * expire);
        return Jwts.builder()
                    .setHeaderParam("typ","JWT")
                    // 名列主体
                    .setSubject(username)
                    // 创建时间
                    .setIssuedAt(nowDate)
                    // 过期时间————7天时间
                    .setExpiration(expireDate)
                    // 加签
                    .signWith(SignatureAlgorithm.HS512, secret)
                    // 合成
                    .compact();
    }

    // 解析JWT
    public Claims getClaimByToken(String jwt){
        try {
            // 使用JWT解析器
            return Jwts.parser()
                        // 密钥
                        .setSigningKey(secret)
                        // 解析jwt
                        .parseClaimsJws(jwt)
                        // 获取body部分
                        .getBody();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }
    // jwt是否过期
    public boolean isTokenExpired(Claims claims){
        // 获取并判断当前是否过期
        return claims.getExpiration().before(new Date());
    }
}
