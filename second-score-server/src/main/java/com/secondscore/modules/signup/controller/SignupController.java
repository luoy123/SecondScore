package com.secondscore.modules.signup.controller;

import com.secondscore.common.api.ApiResponse;
import com.secondscore.common.security.SecurityUtils;
import com.secondscore.modules.signup.dto.ParticipationMarkRequest;
import com.secondscore.modules.signup.dto.RejectSignupRequest;
import com.secondscore.modules.signup.service.SignupService;
import com.secondscore.modules.signup.vo.ParticipationVO;
import com.secondscore.modules.signup.vo.SignupVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/activities/{activityId}/signups")
    @PreAuthorize("hasAuthority('signup:apply')")
    public ApiResponse<Void> signup(@PathVariable Long activityId) {
        signupService.signup(activityId, SecurityUtils.getCurrentUserId());
        return ApiResponse.success();
    }

    @DeleteMapping("/activities/{activityId}/signups/me")
    @PreAuthorize("hasAuthority('signup:apply')")
    public ApiResponse<Void> cancel(@PathVariable Long activityId) {
        signupService.cancelSignup(activityId, SecurityUtils.getCurrentUserId());
        return ApiResponse.success();
    }

    @GetMapping("/signups/me")
    @PreAuthorize("hasAuthority('signup:apply')")
    public ApiResponse<List<SignupVO>> listMySignup() {
        return ApiResponse.success(signupService.listMySignups(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/activities/{activityId}/signups")
    @PreAuthorize("hasAnyAuthority('signup:review:own','signup:review:all')")
    public ApiResponse<List<SignupVO>> listActivitySignups(@PathVariable Long activityId) {
        return ApiResponse.success(signupService.listSignups(
                activityId,
                SecurityUtils.getCurrentUserId(),
                SecurityUtils.getCurrentPermissionCodes()));
    }

    @PostMapping("/signups/{signupId}/approve")
    @PreAuthorize("hasAnyAuthority('signup:review:own','signup:review:all')")
    public ApiResponse<Void> approve(@PathVariable Long signupId) {
        signupService.approveSignup(signupId, SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentPermissionCodes());
        return ApiResponse.success();
    }

    @PostMapping("/signups/{signupId}/reject")
    @PreAuthorize("hasAnyAuthority('signup:review:own','signup:review:all')")
    public ApiResponse<Void> reject(@PathVariable Long signupId, @Valid @RequestBody RejectSignupRequest request) {
        signupService.rejectSignup(signupId, request.getReason(), SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentPermissionCodes());
        return ApiResponse.success();
    }

    @PostMapping("/activities/{activityId}/participations/mark")
    @PreAuthorize("hasAnyAuthority('participation:mark:own','participation:mark:all')")
    public ApiResponse<Void> markParticipation(@PathVariable Long activityId,
                                               @Valid @RequestBody ParticipationMarkRequest request) {
        signupService.markParticipation(activityId, request, SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentPermissionCodes());
        return ApiResponse.success();
    }

    @GetMapping("/activities/{activityId}/participations")
    @PreAuthorize("hasAnyAuthority('participation:mark:own','participation:mark:all')")
    public ApiResponse<List<ParticipationVO>> listParticipation(@PathVariable Long activityId) {
        return ApiResponse.success(signupService.listParticipation(
                activityId,
                SecurityUtils.getCurrentUserId(),
                SecurityUtils.getCurrentPermissionCodes()));
    }
}
