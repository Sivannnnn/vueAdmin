package com.sivan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

// @Data注解的主要作用是提高代码的简洁，使用这个注解可以省去实体类中大量的get()、set()、toString()等方法。
@Data
// Serializable使序列化
public class BaseEntity implements Serializable {
    // @TableId注解是专门用在主键上的注解
    // id的生成规则，自增长生成id
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private LocalDateTime created;
    private LocalDateTime updated;
    private Integer statu;
}
