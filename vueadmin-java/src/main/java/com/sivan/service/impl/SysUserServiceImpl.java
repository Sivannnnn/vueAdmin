package com.sivan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sivan.entity.SysMenu;
import com.sivan.entity.SysRole;
import com.sivan.entity.SysUser;
import com.sivan.mapper.SysUserMapper;
import com.sivan.service.SysMenuService;
import com.sivan.service.SysRoleService;
import com.sivan.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sivan.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Sivan
 * @since 2022-10-09
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    SysRoleService sysRoleService;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    SysUserMapper sysUserMapper;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    SysMenuService sysMenuService;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    RedisUtil redisUtil;

    @Override
    public SysUser getByUsername(String username) {
        // myBatis写法————getOne方法最终得到的是 实体类对象
        return getOne(new QueryWrapper<SysUser>().eq("username",username));
    }

    @Override
    public String getUserAuthorityInfo(Long userId) {
        // 获取userId
        SysUser sysUser = sysUserMapper.selectById(userId);

        // ROLE_admin,sys:user:list....
        String authority = "";

        // 判断是否有权限缓存
        if(redisUtil.hasKey("GrantedAuthority:" + sysUser.getUsername())) {
            // 强转为String
            authority = (String) redisUtil.get("GrantedAuthority:" + sysUser.getUsername());
        }else{
            // 获取角色信息
            // 先以"user_id"查询sys_user_id表中的"role_id",再以查询结果作为条件查询sys_role表中的所有数据
            List<SysRole> roles = sysRoleService.list(new QueryWrapper<SysRole>()
                    .inSql("id", "select role_id from sys_user_role where user_id = " + userId));
            // 判断查询的数据是否存在
            if(roles.size() > 0){
                // 使用流的形式遍历每一项，再将在后面加逗号
                String roleCodes = roles.stream().map(r -> "ROLE_" + r.getCode()).collect(Collectors.joining(","));
                authority = roleCodes.concat(",");
            }

            // 获取菜单操作编码
            // 获取menu_id
            List<Long> menuIds = sysUserMapper.getNavMenuIds(userId);
            // 判断查询的数据是否存在
            if(menuIds.size() > 0){
                List<SysMenu> menus = sysMenuService.listByIds(menuIds);
                // 使用流的形式遍历每一项，再将在后面加逗号
                String menuPerms = menus.stream().map(r -> r.getPerms()).collect(Collectors.joining(","));
                authority = authority.concat(menuPerms);
            }

            // 配置缓存机制
            redisUtil.set("GrantedAuthority:" + sysUser.getUsername(),authority,60 * 60);
        }

        return authority;
    }

    @Override
    public void clearUserAuthorityInfo(String username) {
        // 删除用户相关
        redisUtil.del("GrantedAuthority:" + username);
    }

    @Override
    public void clearUserAuthorityInfoByRoleId(Long roleId) {
        // 删除用户相关
        List<SysUser> sysUsers = this.list(new QueryWrapper<SysUser>()
                .inSql("id","select user_id from sys_user_role where role_id = " + roleId));
        sysUsers.forEach(u -> {
            this.clearUserAuthorityInfo(u.getUsername());
        });
    }

    @Override
    public void clearUserAuthorityInfoByRoleMenuId(Long menuId) {
        List<SysUser> sysUsers = sysUserMapper.listByMenuId(menuId);
        sysUsers.forEach(u -> {
            this.clearUserAuthorityInfo(u.getUsername());
        });
    }
}
