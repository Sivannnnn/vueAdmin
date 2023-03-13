package com.sivan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sivan.entity.SysRole;
import com.sivan.mapper.SysRoleMapper;
import com.sivan.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public List<SysRole> listRoleByUserId(Long userId) {

        //先从关联表中查出role
        List<SysRole> sysRoles = this.list(new QueryWrapper<SysRole>().
                inSql("id", "Select role_id from sys_user_role where user_id = " + userId));

        return sysRoles;
    }
}
