package com.sivan.security;

import com.sivan.entity.SysUser;
import com.sivan.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  查询用户的相关数据
 */

// @Service注解用于类上,标记当前类是一个service类,加上该注解会将当前类自动注入到spring容器中
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    // 引入sysUserService，通过它可以拿到用户数据
    SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 获取数据库中的信息
        SysUser sysUser = sysUserService.getByUsername(username);
        // 判断sysUser是否为空
        if(sysUser == null){
            throw new UsernameNotFoundException("用户名或密码不正确");
        }

        return new AccountUser(sysUser.getId(),sysUser.getUsername(),sysUser.getPassword(),getUserAuthority(sysUser.getId()));
    }

    /**
     * 获取用户权限信息（角色，菜单权限）
     */
    public List<GrantedAuthority> getUserAuthority(Long userId){

        // 角色(ROLE_admin)、菜单操作权限 sys:user:list
        String authority = sysUserService.getUserAuthorityInfo(userId); // ROLE_admin,sys:user:list....
        // 使用AuthorityUtils工具转载
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
    }
}
