package com.secondscore.modules.credit.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreditRecordVO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNo;
    private Long activityId;
    private String activityTitle;
    private Long termId;
    private String termName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal credit;
    private String grantStatus;
    private Long grantBy;
    private String grantByName;
    private LocalDateTime grantTime;
    private Long revokeBy;
    private String revokeByName;
    private LocalDateTime revokeTime;
    private String revokeReason;
}

