package com.sivan.service;

import com.sivan.entity.SysRole;
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
public interface SysRoleService extends IService<SysRole> {

    List<SysRole> listRoleByUserId(Long userId);


}
