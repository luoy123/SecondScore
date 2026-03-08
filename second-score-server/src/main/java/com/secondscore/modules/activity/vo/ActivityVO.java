package com.secondscore.modules.activity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ActivityVO {
    private Long id;
    private String title;
    private Long categoryId;
    private String categoryName;
    private Long termId;
    private String termName;
    private Long organizerId;
    private String organizerName;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime signupStart;
    private LocalDateTime signupEnd;
    private Integer capacity;
    private Integer signupCount;
    private BigDecimal credit;
    private String status;
    private String description;
}

