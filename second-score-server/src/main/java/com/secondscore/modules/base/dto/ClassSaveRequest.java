package com.secondscore.modules.base.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassSaveRequest {
    @NotNull(message = "专业ID不能为空")
    private Long majorId;
    @NotBlank(message = "班级名称不能为空")
    private String className;
    @NotNull(message = "年级不能为空")
    private Integer grade;
    private Integer status = 1;
}

