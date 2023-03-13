package com.sivan.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 为了保持与前端mock.js返回的导航栏数据一致
 */

/**
 * {
 *                     name: 'SysUser',
 *                     title: '用户管理',
 *                     icon: 'el-icon-s-custom',
 *                     path: '/sys/users',
 *                     component: 'sys/User',
 *                     children: []
 *                 }
 */

// @Data注解的主要作用是提高代码的简洁，使用这个注解可以省去实体类中大量的get()、 set()、 toString()等方法。
@Data
// 使之序列化
public class SysMenuDto implements Serializable {
    // 赋予id方便找得到该实体
    private Long id;
    // 生成实体
    private String name;
    private String title;
    private String icon;
    private String path;
    private String component;
    private List<SysMenuDto> children = new ArrayList<>(); // 生成子类
}
