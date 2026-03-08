package com.secondscore.modules.analysis.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TermTrendVO {
    private Long termId;
    private String termName;
    private BigDecimal totalCredit;
}

