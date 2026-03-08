package com.secondscore.modules.signup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity_participation")
public class ActivityParticipation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long studentId;
    private String attendanceStatus;
    private Long markedBy;
    private LocalDateTime markedTime;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

