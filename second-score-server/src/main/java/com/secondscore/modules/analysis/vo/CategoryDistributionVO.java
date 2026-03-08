package com.secondscore.modules.analysis.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategoryDistributionVO {
    private Long categoryId;
    private String categoryName;
    private BigDecimal totalCredit;
}

