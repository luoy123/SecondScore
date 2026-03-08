package com.secondscore.modules.signup.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectSignupRequest {
    @NotBlank(message = "拒绝原因不能为空")
    private String reason;
}

