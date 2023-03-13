package com.sivan.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sivan.common.dto.SysMenuDto;
import com.sivan.entity.SysMenu;
import com.sivan.entity.SysUser;
import com.sivan.mapper.SysMenuMapper;
import com.sivan.mapper.SysUserMapper;
import com.sivan.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sivan.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Sivan
 * @since 2022-10-09
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    SysUserService sysUserService;
    // @Autowired可以标注在属性上、方法上和构造器上,来完成自动装配。
    @Autowired
    SysUserMapper sysUserMapper;



    @Override
    public List<SysMenuDto> getCurrentUserNav() {
        // 获取当前用户名
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 获取当前用户信息
        SysUser sysUser = sysUserService.getByUsername(username);

        // 获取menuId
        List<Long> menuIds = sysUserMapper.getNavMenuIds(sysUser.getId());
        List<SysMenu> menus = this.listByIds(menuIds);
        
        // 转树状结构
        List<SysMenu> menuTree = buildTreeMenu(menus);
        
        // 实体转DTO
        return convert(menuTree);
    }

    @Override
    public List<SysMenu> tree() {
        // 获取所有菜单信息
        List<SysMenu> sysMenus = this.list(new QueryWrapper<SysMenu>().orderByAsc("orderNum"));
        // 转成树状结构
        return buildTreeMenu(sysMenus);
    }

    private List<SysMenuDto> convert(List<SysMenu> menuTree) {
        List<SysMenuDto> menuDtos = new ArrayList<>();
        // for循环转型
        menuTree.forEach(m -> {
            SysMenuDto dto = new SysMenuDto();
            dto.setId(m.getId());
            dto.setName(m.getName());
            dto.setTitle(m.getName());
            dto.setComponent(m.getComponent());
            dto.setPath(m.getPath());

            if (m.getChildren().size() > 0){
                // 子节点调用当前方法进行再次转换
                dto.setChildren(convert(m.getChildren()));
            }
            menuDtos.add(dto);
        });
        return menuDtos;
    }

    private List<SysMenu> buildTreeMenu(List<SysMenu> menus) {
        // 重新找到上下级的关系
        List<SysMenu> finalMenus = new ArrayList<>();

        // 先各自找到各自的孩子
        for (SysMenu menu : menus) {
            for (SysMenu e : menus){
                // 判断该节点是否有子节点
                if (menu.getId() == e.getParentId()) {
                    menu.getChildren().add(e);
                }
            }
            //提取出父节点
            if (menu.getParentId() == 0L) {
                finalMenus.add(menu);
            }
        }
        System.out.println(JSONUtil.toJsonStr(finalMenus));
        return finalMenus;
    }
}
