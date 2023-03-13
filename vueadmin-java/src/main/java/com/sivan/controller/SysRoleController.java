package com.sivan.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sivan.common.lang.Const;
import com.sivan.common.lang.Result;
import com.sivan.entity.SysRole;
import com.sivan.entity.SysRoleMenu;
import com.sivan.entity.SysUserRole;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Sivan
 * @since 2022-10-09
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {

    /**
     * 编写用户接口
     * @param id
     * @return
     */

    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result info(@PathVariable("id") Long id) {
        // 通过传过来的 id 来查询详情记录
        SysRole sysRole = sysRoleService.getById(id);
        // 查出关联表
        List<SysRoleMenu> roleMenus = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().eq("role_id", id));

        // 使用流的方式获取角色相关联的菜单id
        List<Long> menuIds = roleMenus.stream().map(p -> p.getMenuId()).collect(Collectors.toList());
        // 将menuIds赋给sysRole
        sysRole.setMenuIds(menuIds);

        return Result.succ(sysRole);
    }

    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/list")
    public Result list(String name) {
        // 获取页码
        Page<SysRole> pageData = sysRoleService.page(getPage(),
                new QueryWrapper<SysRole>()
                        // 当name不为空时，查询name的值为name
                            .like(StrUtil.isNotBlank(name),"name",name)
        );
        return Result.succ(pageData);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:role:save')")
    // @Validated 加入之后后者的实体需要被检验
    // @RequestBody 传过来的是表单 所以要要从requestBody里面获取
    public Result save(@Validated @RequestBody SysRole sysRole) {
        // 值不为空时，创建必要数据
        sysRole.setCreated(LocalDateTime.now());
        // 角色设置为正常状态————0
        sysRole.setStatu(Const.STATUS_ON);

        sysRoleService.save(sysRole);

        return Result.succ(sysRole);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:role:update')")
    public Result update(@Validated @RequestBody SysRole sysRole) {
        // 设置更新时间
        sysRole.setUpdated(LocalDateTime.now());

        sysRoleService.updateById(sysRole);

        // 更新缓存
        sysUserService.clearUserAuthorityInfoByRoleId(sysRole.getId());

        return Result.succ(sysRole);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    // @Transactional 事务，保证要么前进，要么回退
    @Transactional
    public Result delete(@RequestBody Long[] ids) {
        // 批量删除 Arrays.asList————将数组转成集合
        sysRoleService.removeByIds(Arrays.asList(ids));
        // 删除所有相关的中间表
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id",ids));
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().in("role_id",ids));

        // 缓存同步删除
        Arrays.stream(ids).forEach(id -> {
            // 更新缓存
            sysUserService.clearUserAuthorityInfoByRoleId(id);
        });

        return Result.succ("");
    }

    // @Transactional 事务，保证要么前进，要么回退
    @Transactional
    @PostMapping("/perm/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:perm')")
    public Result info(@PathVariable("roleId")Long roleId,@RequestBody Long[] menuIds) {

        List<SysRoleMenu> sysRoleMenus = new ArrayList<>();

        // 将menuIds拆分成多条记录
        Arrays.stream(menuIds).forEach(menuId -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);
            // 存于sysRoleMenus里
            sysRoleMenus.add(roleMenu);
        });

        // 先删除原来的记录，再保存新的
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id",roleId));
        sysRoleMenuService.saveBatch(sysRoleMenus);

        // 删除缓存
        sysUserService.clearUserAuthorityInfoByRoleId(roleId);

        return Result.succ(menuIds);
    }
}
