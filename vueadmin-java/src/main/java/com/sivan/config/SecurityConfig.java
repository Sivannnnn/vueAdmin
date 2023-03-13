package com.sivan.config;

import com.sivan.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 权限安全配置类
 */

// @Configuration注解的作用:声明一个类为配置类,用于取代bean.xml配置文件注册bean对象。
@Configuration
// @EnableWebSecurity是开启SpringSecurity的默认行为
@EnableWebSecurity
// @EnableGlobalMethodSecurity注解,用于开启Spring环境的方法级安全
// prePostEnabled = true设置所有的方法都要进行权限认证
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    // 引入异常处理的类
    LoginFailureHandler loginFailureHandler;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    // 引入成功处理的类
    LoginSuccessHandler loginSuccessHandler;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    CaptchaFilter captchaFilter;
    // @Bean注解用于告诉方法,产生一个Bean对象,然后这个Bean对象交给Spring管理。
    @Autowired
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    // @Bean注解用于告诉方法,产生一个Bean对象,然后这个Bean对象交给Spring管理。
    @Autowired
    JwtAcessDeniedHandler jwtAcessDeniedHandler;
    // @Bean注解用于告诉方法,产生一个Bean对象,然后这个Bean对象交给Spring管理。
    @Autowired
    UserDetailServiceImpl userDetailService;
    // @Bean注解用于告诉方法,产生一个Bean对象,然后这个Bean对象交给Spring管理。
    @Autowired
    JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    // @Bean注解用于告诉方法,产生一个Bean对象,然后这个Bean对象交给Spring管理。
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        // 构造器里配置认证管理器
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager());
        return jwtAuthenticationFilter;
    }
    // @Bean注解用于告诉方法,产生一个Bean对象,然后这个Bean对象交给Spring管理。
    @Bean
    // 密码的加密形式———密码构造器
    BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 白名单
    private static final String[] URL_WHITELIST = {
            "/login",
            "/logout",
            "/captcha",
            "favicon.ico"
    };

    protected void configure(HttpSecurity http) throws Exception {
        // 关闭预防csrf攻击
        http.cors().and().csrf().disable()
        // 登录配置————使用UserNamePasswordAuthorizeFilter过滤器
                .formLogin()
                    .successHandler(loginSuccessHandler)
                    .failureHandler(loginFailureHandler)
        // 登出配置
                .and()
                .logout()
                .logoutSuccessHandler(jwtLogoutSuccessHandler)
        // 禁用session
                .and()
                .sessionManagement()
                // 生成规则————不生成Session的策略
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        // 配置拦截规则
                .and()
                .authorizeRequests()
                // 配置白名单
                .antMatchers(URL_WHITELIST).permitAll()
                // 必须经过登录才能访问
                .anyRequest().authenticated()
        // 异常处理器
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                //权限不足处理器
                .accessDeniedHandler(jwtAcessDeniedHandler)
        // 配置自定义的过滤器
                .and()
                .addFilter(jwtAuthenticationFilter())
                // 图片验证码认证处理器放置于用户账号密码认证处理器之前
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)

        ;
    }

    // 配置重写Service
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }
}
