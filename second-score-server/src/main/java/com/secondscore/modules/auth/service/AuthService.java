package com.secondscore.modules.auth.service;

import com.secondscore.modules.auth.dto.ChangePasswordRequest;
import com.secondscore.modules.auth.dto.LoginRequest;
import com.secondscore.modules.auth.vo.CaptchaResponse;
import com.secondscore.modules.auth.vo.LoginResponse;
import com.secondscore.modules.user.vo.UserVO;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    CaptchaResponse captcha();

    UserVO me(Long userId);

    void changePassword(Long userId, ChangePasswordRequest request);
}
