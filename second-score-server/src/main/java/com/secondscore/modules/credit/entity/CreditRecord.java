package com.secondscore.modules.credit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("credit_record")
public class CreditRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long activityId;
    private Long termId;
    private Long categoryId;
    private BigDecimal credit;
    private String grantStatus;
    private Long grantBy;
    private LocalDateTime grantTime;
    private Long revokeBy;
    private LocalDateTime revokeTime;
    private String revokeReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

