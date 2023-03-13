package com.sivan.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sivan.service.*;
import com.sivan.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    HttpServletRequest req;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    RedisUtil redisUtil;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    SysUserService sysUserService;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    SysRoleService sysRoleService;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    SysMenuService sysMenuService;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    SysUserRoleService sysUserRoleService;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    SysRoleMenuService sysRoleMenuService;

    /** 获取页码
     *
     * @return
     */
    public Page getPage(){
        // 获取当前页码
        int current = ServletRequestUtils.getIntParameter(req,"current",1);
        // 每一页多少条数据——默认十条
        int size = ServletRequestUtils.getIntParameter(req,"size",10);

        return new Page(current,size);
    }
}
