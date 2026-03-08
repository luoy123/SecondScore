package com.secondscore.modules.signup.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SignupVO {
    private Long id;
    private Long activityId;
    private Long studentId;
    private String studentName;
    private String studentNo;
    private LocalDateTime applyTime;
    private String reviewStatus;
    private Long reviewBy;
    private String reviewerName;
    private LocalDateTime reviewTime;
    private String rejectReason;
    private String signupStatus;
}

