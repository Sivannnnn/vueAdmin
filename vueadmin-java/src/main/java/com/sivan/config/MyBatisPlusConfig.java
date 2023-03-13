package com.sivan.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// springBoot配置方式
// @Configuration：使该文件能识别到该类使xml配置文件
@Configuration
// @mapperScan：扫描mapper所存放接口的位置
@MapperScan("com.sivan.mapper")
public class MyBatisPlusConfig {
    // @Bean：<Bean>标签，子属性
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件：给mybatis plus传递一个参数，就会自动完成一个分页操作
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        // 防全表更新插件：当update更新某一条id数据的时候，如果不满足规范，就会更新全表，此插件能防止更新全表。
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false
     * 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    // @Bean：<Bean>标签，子属性
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }
}