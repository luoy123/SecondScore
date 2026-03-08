package com.secondscore.modules.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("base_term")
public class BaseTerm {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String termName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer isCurrent;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

