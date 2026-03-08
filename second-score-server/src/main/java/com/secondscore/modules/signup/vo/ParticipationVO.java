package com.secondscore.modules.signup.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParticipationVO {
    private Long id;
    private Long activityId;
    private Long studentId;
    private String studentName;
    private String studentNo;
    private String attendanceStatus;
    private Long markedBy;
    private String markerName;
    private LocalDateTime markedTime;
    private String remark;
}

