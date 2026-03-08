package com.secondscore.modules.signup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity_signup")
public class ActivitySignup {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long studentId;
    private LocalDateTime applyTime;
    private String reviewStatus;
    private Long reviewBy;
    private LocalDateTime reviewTime;
    private String rejectReason;
    private String signupStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

