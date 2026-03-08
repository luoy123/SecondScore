package com.secondscore.modules.credit.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PersonalCreditSummaryVO {
    private Long studentId;
    private String studentName;
    private Long termId;
    private String termName;
    private BigDecimal totalCredit;
}

