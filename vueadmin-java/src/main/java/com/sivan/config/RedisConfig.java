package com.sivan.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// @Configuration注解的作用:声明一个类为配置类,用于取代bean.xml配置文件注册bean对象。
@Configuration
public class RedisConfig {
    // @Bean注解用于告诉方法,产生一个Bean对象,然后这个Bean对象交给Spring管理。
    @Bean
    // 重写Redis中的Template序列化的方式
    RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 序列化方式：
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(new ObjectMapper());
        // key————String
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // value————JSON
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // hash值
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        return redisTemplate;
    }
}
