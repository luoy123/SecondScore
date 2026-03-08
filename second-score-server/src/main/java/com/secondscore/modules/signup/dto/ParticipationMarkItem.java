package com.secondscore.modules.signup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParticipationMarkItem {
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    @NotBlank(message = "参与状态不能为空")
    private String attendanceStatus;
    private String remark;
}

