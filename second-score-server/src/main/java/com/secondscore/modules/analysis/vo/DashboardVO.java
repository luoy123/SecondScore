package com.secondscore.modules.analysis.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardVO {
    private Long totalActivities;
    private Long totalSignups;
    private BigDecimal totalGrantedCredit;
    private Long totalCreditedStudents;
}

