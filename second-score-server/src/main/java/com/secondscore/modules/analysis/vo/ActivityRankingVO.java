package com.secondscore.modules.analysis.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActivityRankingVO {
    private Long activityId;
    private String activityTitle;
    private Long participantCount;
    private BigDecimal totalCredit;
}

