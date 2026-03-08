package com.secondscore.modules.user.controller;

import com.secondscore.common.api.ApiResponse;
import com.secondscore.common.security.SecurityUtils;
import com.secondscore.modules.user.dto.ProfileUpdateRequest;
import com.secondscore.modules.user.service.SysUserService;
import com.secondscore.modules.user.vo.AvatarUploadVO;
import com.secondscore.modules.user.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final SysUserService sysUserService;

    @GetMapping
    public ApiResponse<UserVO> myProfile() {
        return ApiResponse.success(sysUserService.getCurrentUserProfile(SecurityUtils.getCurrentUserId()));
    }

    @PutMapping
    public ApiResponse<Void> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        sysUserService.updateSelfProfile(SecurityUtils.getCurrentUserId(), request);
        return ApiResponse.success();
    }

    @PostMapping("/avatar")
    public ApiResponse<AvatarUploadVO> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = sysUserService.updateAvatar(SecurityUtils.getCurrentUserId(), file);
        AvatarUploadVO vo = new AvatarUploadVO();
        vo.setAvatarUrl(avatarUrl);
        return ApiResponse.success(vo);
    }
}
