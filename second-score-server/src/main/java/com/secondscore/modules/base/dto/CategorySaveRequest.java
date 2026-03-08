package com.secondscore.modules.base.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategorySaveRequest {
    @NotBlank(message = "类别名称不能为空")
    private String categoryName;
    @NotNull(message = "默认学分不能为空")
    @DecimalMin(value = "0.0", message = "默认学分不能小于0")
    private BigDecimal defaultCredit;
    @NotNull(message = "学期上限不能为空")
    @DecimalMin(value = "0.0", message = "学期上限不能小于0")
    private BigDecimal maxCreditPerTerm;
    private Integer status = 1;
}

