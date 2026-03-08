package com.secondscore.modules.base.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CollegeSaveRequest {
    @NotBlank(message = "学院编码不能为空")
    private String collegeCode;
    @NotBlank(message = "学院名称不能为空")
    private String collegeName;
    private Integer status = 1;
}

