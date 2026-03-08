package com.secondscore.modules.base.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MajorSaveRequest {
    @NotNull(message = "学院ID不能为空")
    private Long collegeId;
    @NotBlank(message = "专业编码不能为空")
    private String majorCode;
    @NotBlank(message = "专业名称不能为空")
    private String majorName;
    private Integer status = 1;
}

