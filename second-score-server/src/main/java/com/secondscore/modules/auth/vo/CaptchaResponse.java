package com.secondscore.modules.auth.vo;

import lombok.Data;

@Data
public class CaptchaResponse {
    private String captchaId;
    private String imageBase64;
    private Long expireSeconds;
}
