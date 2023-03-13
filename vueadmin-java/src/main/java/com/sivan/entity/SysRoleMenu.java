package com.sivan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Sivan
 * @since 2022-10-09
 */
@Data
public class SysRoleMenu {

    private static final long serialVersionUID = 1L;

    // @TableId注解是专门用在主键上的注解
    // id的生成规则，自增长生成id
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private Long roleId;

    private Long menuId;


}
