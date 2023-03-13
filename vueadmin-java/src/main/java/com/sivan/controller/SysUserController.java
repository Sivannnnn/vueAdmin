package com.sivan.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sivan.common.dto.PassDto;
import com.sivan.common.lang.Const;
import com.sivan.common.lang.Result;
import com.sivan.entity.SysRole;
import com.sivan.entity.SysUser;
import com.sivan.entity.SysUserRole;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Sivan
 * @since 2022-10-09
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController {

    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    BCryptPasswordEncoder passwordEncoder;


    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result info(@PathVariable("id") Long id) {
        // 获取id
        SysUser sysUser = sysUserService.getById(id);
        // 使用断言判断是否为空
        Assert.notNull(sysUser,"找不到该管理员");
        // 从关联表中查出role
        List<SysRole> roles = sysRoleService.listRoleByUserId(id);
        sysUser.setSysRoles(roles);

        return Result.succ(sysUser);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result list(String username) {
        // 首先获取页码页数，然后当查询数据不为空的时候查询数据
        Page<SysUser> pageData =  sysUserService.page(getPage(),new QueryWrapper<SysUser>()
                .like(StrUtil.isNotBlank(username),"username",username));

        pageData.getRecords().forEach(u -> {
            u.setSysRoles(sysRoleService.listRoleByUserId(u.getId()));
        });

        return Result.succ(pageData);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:user:save')")
    // @Validated 加入之后后者的实体需要被检验
    // @RequestBody 传过来的是表单 所以要要从requestBody里面获取
    public Result save(@Validated @RequestBody SysUser sysUser) {

        sysUser.setCreated(LocalDateTime.now());
        sysUser.setStatu(Const.STATUS_ON);

        // 密码加密 默认初始密码————888888
        String password = passwordEncoder.encode(Const.DEFAULT_PASSWORD);
        sysUser.setPassword(password);

        // 默认头像
        sysUser.setAvatar(Const.DEFALUT_AVATAR);

        sysUserService.save(sysUser);

        return Result.succ(sysUser);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:user:update')")
    // @Validated 加入之后后者的实体需要被检验
    // @RequestBody 传过来的是表单 所以要要从requestBody里面获取
    public Result update(@Validated @RequestBody SysUser sysUser) {

        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);

        return Result.succ(sysUser);
    }

    // 设计两次查库 使用事务
    @Transactional
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    public Result delete(@RequestBody Long[] ids) {
        // 删除有关的中间表
        sysUserService.removeByIds(Arrays.asList(ids));
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id",ids));


        return Result.succ("");
    }

    @Transactional
    @PostMapping("/role/{userId}")
    @PreAuthorize("hasAuthority('sys:user:role')")
    public Result rolePerm(@PathVariable("userId") Long userId,@RequestBody Long[] roleIds) {
        // 保存至List里
        List<SysUserRole> userRoles = new ArrayList<>();

        Arrays.stream(roleIds).forEach(r -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(r);
            sysUserRole.setUserId(userId);
            // 保存至userRoles里
            userRoles.add(sysUserRole);
        });
        // 进行批量删除
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id",userId));
        sysUserRoleService.saveBatch(userRoles);

        // 删除缓存
        SysUser sysUser = sysUserService.getById(userId);
        sysUserService.clearUserAuthorityInfo(sysUser.getUsername());

        return Result.succ("");
    }

    @PostMapping("/repass")
    @PreAuthorize("hasAuthority('sys:user:repass')")
    public Result repass(@RequestBody Long userId) {

        SysUser sysUser = sysUserService.getById(userId);

        sysUser.setPassword(passwordEncoder.encode(Const.DEFAULT_PASSWORD));
        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);

        return Result.succ("");
    }

    @PostMapping("/updatePass")
    public Result updatePass(@Validated @RequestBody PassDto passDto, Principal principal) {
        // 获取哪个User
        SysUser sysUser = sysUserService.getByUsername(principal.getName());
        // 对旧密码和新密码进行匹配
        boolean matches = passwordEncoder.matches(passDto.getCurrentPass(), sysUser.getPassword());
        if(!matches) {
            return Result.fail("旧密码不正确");
        }

        sysUser.setPassword(passwordEncoder.encode(passDto.getPassword()));
        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);

        return Result.succ("");
    }
}
