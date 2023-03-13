package com.sivan.service;

import com.sivan.common.dto.SysMenuDto;
import com.sivan.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Sivan
 * @since 2022-10-09
 */
public interface SysMenuService extends IService<SysMenu> {
    List<SysMenuDto> getCurrentUserNav();
    List<SysMenu> tree();

}
