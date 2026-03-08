package com.secondscore.modules.signup.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ParticipationMarkRequest {
    @Valid
    @NotEmpty(message = "登记列表不能为空")
    private List<ParticipationMarkItem> items;
}

