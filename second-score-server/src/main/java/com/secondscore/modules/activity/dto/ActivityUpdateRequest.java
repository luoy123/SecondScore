package com.secondscore.modules.activity.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ActivityUpdateRequest {
    @NotBlank(message = "活动标题不能为空")
    private String title;
    @NotNull(message = "活动类别不能为空")
    private Long categoryId;
    @NotNull(message = "学期不能为空")
    private Long termId;
    private Long organizerId;
    private String location;
    @NotNull(message = "活动开始时间不能为空")
    private LocalDateTime startTime;
    @NotNull(message = "活动结束时间不能为空")
    private LocalDateTime endTime;
    @NotNull(message = "报名开始时间不能为空")
    private LocalDateTime signupStart;
    @NotNull(message = "报名结束时间不能为空")
    private LocalDateTime signupEnd;
    @NotNull(message = "人数上限不能为空")
    private Integer capacity;
    @NotNull(message = "学分不能为空")
    @DecimalMin(value = "0.0", message = "学分不能小于0")
    private BigDecimal credit;
    private String description;
}

