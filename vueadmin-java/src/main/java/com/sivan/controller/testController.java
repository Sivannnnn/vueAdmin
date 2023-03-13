package com.sivan.controller;

import com.sivan.common.lang.Result;
import com.sivan.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController的作用等同于@Controller + @ResponseBody
// @Controller 将当前修饰的类注入SpringBoot IOC容器，使得从该类所在的项目跑起来的过程中，这个类就被实例化。
// @ResponseBody 它的作用简短截说就是指该类中所有的API接口返回的数据，甭管你对应的方法返回Map或是其他Object，
//               它会以Json字符串的形式返回给客户端
@RestController
public class testController {

    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配
    @Autowired
    SysUserService sysUserService;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    // @PreAuthorize注解会在方法执行前进行权限验证,支持Spring EL表达式,它是基于方法注解的权限解决方案
    @PreAuthorize("hasRole('admin')")
    // @GetMapping请求方式
    @GetMapping("/test")
    // 返回test类
    public Result test(){
        // 返回SysUserService类的数据列表
        // return Result.fail("test~~~未知错误");
        return Result.succ(sysUserService.list());
    }

    // 普通用户、超级管理员
    // @PreAuthorize注解会在方法执行前进行权限验证,支持Spring EL表达式,它是基于方法注解的权限解决方案
    @PreAuthorize("hasAnyAuthority('sys:user:list')")
    // @GetMapping请求方式
    @GetMapping("/test/pass")
    // 返回pass类
    public Result pass(){
        // 获取加密后密码
        String password = bCryptPasswordEncoder.encode("111111");
        // 匹配密码与加密后密码
        boolean matches = bCryptPasswordEncoder.matches("111111", password);
        //输出meches是否正确
        System.out.println("匹配结果："+matches);
        // 返回SysUserService类的数据列表
        // return Result.fail("test~~~未知错误");
        return Result.succ(password);
    }
}