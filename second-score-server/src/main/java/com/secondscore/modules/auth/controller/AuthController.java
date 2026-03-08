package com.secondscore.modules.auth.controller;

import com.secondscore.common.api.ApiResponse;
import com.secondscore.common.security.SecurityUtils;
import com.secondscore.modules.auth.dto.ChangePasswordRequest;
import com.secondscore.modules.auth.dto.LoginRequest;
import com.secondscore.modules.auth.service.AuthService;
import com.secondscore.modules.auth.vo.CaptchaResponse;
import com.secondscore.modules.auth.vo.LoginResponse;
import com.secondscore.modules.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/captcha")
    public ApiResponse<CaptchaResponse> captcha() {
        return ApiResponse.success(authService.captcha());
    }

    @GetMapping("/me")
    public ApiResponse<UserVO> me() {
        return ApiResponse.success(authService.me(SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(SecurityUtils.getCurrentUserId(), request);
        return ApiResponse.success();
    }
}
