package com.secondscore.modules.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("base_major")
public class BaseMajor {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long collegeId;
    private String majorCode;
    private String majorName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

