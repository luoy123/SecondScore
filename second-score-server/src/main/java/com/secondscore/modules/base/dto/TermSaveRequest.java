package com.secondscore.modules.base.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TermSaveRequest {
    @NotBlank(message = "学期名称不能为空")
    private String termName;
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
    private Integer isCurrent = 0;
    private Integer status = 1;
}

