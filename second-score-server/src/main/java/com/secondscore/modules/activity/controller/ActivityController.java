package com.secondscore.modules.activity.controller;

import com.secondscore.common.api.ApiResponse;
import com.secondscore.common.security.SecurityUtils;
import com.secondscore.modules.activity.dto.ActivityCreateRequest;
import com.secondscore.modules.activity.dto.ActivityUpdateRequest;
import com.secondscore.modules.activity.service.ActivityService;
import com.secondscore.modules.activity.vo.ActivityVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('activity:manage:own','activity:manage:all')")
    public ApiResponse<Long> create(@Valid @RequestBody ActivityCreateRequest request) {
        return ApiResponse.success(activityService.create(
                request,
                SecurityUtils.getCurrentUserId(),
                SecurityUtils.getCurrentPermissionCodes()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('activity:manage:own','activity:manage:all')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ActivityUpdateRequest request) {
        activityService.update(id, request, SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentPermissionCodes());
        return ApiResponse.success();
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyAuthority('activity:manage:own','activity:manage:all')")
    public ApiResponse<Void> publish(@PathVariable Long id) {
        activityService.publish(id, SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentPermissionCodes());
        return ApiResponse.success();
    }

    @PostMapping("/{id}/finish")
    @PreAuthorize("hasAnyAuthority('activity:manage:own','activity:manage:all')")
    public ApiResponse<Void> finish(@PathVariable Long id) {
        activityService.finish(id, SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentPermissionCodes());
        return ApiResponse.success();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('activity:view')")
    public ApiResponse<List<ActivityVO>> list(@RequestParam(required = false) String title,
                                              @RequestParam(required = false) Long termId,
                                              @RequestParam(required = false) String status) {
        return ApiResponse.success(activityService.list(
                title,
                termId,
                status,
                SecurityUtils.getCurrentUserId(),
                SecurityUtils.getCurrentPermissionCodes()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('activity:view')")
    public ApiResponse<ActivityVO> detail(@PathVariable Long id) {
        return ApiResponse.success(activityService.detail(
                id,
                SecurityUtils.getCurrentUserId(),
                SecurityUtils.getCurrentPermissionCodes()));
    }
}
