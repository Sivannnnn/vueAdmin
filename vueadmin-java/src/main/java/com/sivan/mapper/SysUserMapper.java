package com.sivan.mapper;

import com.sivan.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Sivan
 * @since 2022-10-09
 */

// @Repository注解便属于最先引入的一批,它用于将数据访问层 (DAO 层 ) 的类标识为 Spring Bean。
@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {
    List<Long> getNavMenuIds(Long userId);

    List<SysUser> listByMenuId(Long menuId);
}
