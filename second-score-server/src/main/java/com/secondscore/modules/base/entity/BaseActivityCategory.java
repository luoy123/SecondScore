package com.secondscore.modules.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("base_activity_category")
public class BaseActivityCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String categoryName;
    private BigDecimal defaultCredit;
    private BigDecimal maxCreditPerTerm;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

