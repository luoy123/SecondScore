package com.secondscore.modules.auth.service;

import com.secondscore.modules.auth.vo.CaptchaResponse;

public interface CaptchaService {
    CaptchaResponse generateCaptcha();

    void validateCaptcha(String captchaId, String captchaCode);
}
