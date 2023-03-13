package com.sivan.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
  *  图片验证码的生成规则
**/

// @Configuration注解的作用:声明一个类为配置类,用于取代bean.xml配置文件注册bean对象。
@Configuration
public class KaptchaConfig {
    // @Bean注解用于告诉方法,产生一个Bean对象,然后这个Bean对象交给Spring管理。
    @Bean
    DefaultKaptcha producer(){
        // 定义验证码的样式
        Properties properties = new Properties();
        properties.put("kaptcha.border", "no");
        properties.put("kaptcha.textproducer.font.color", "black");
        properties.put("kaptcha.textproducer.char.space", "4");
        properties.put("kaptcha.image.height", "40");
        properties.put("kaptcha.image.width", "120");
        properties.put("kaptcha.textproducer.font.size", "30");
        // 将定义好的样式配置进去
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }
}
