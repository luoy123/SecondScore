package com.secondscore.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @NotBlank(message = "姓名不能为空")
    private String realName;
    private String phone;
    private String email;
    private Long collegeId;
    private Long majorId;
    private Long classId;
}

