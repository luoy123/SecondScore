package com.secondscore.modules.credit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreditRevokeRequest {
    @NotBlank(message = "撤销原因不能为空")
    private String reason;
}

