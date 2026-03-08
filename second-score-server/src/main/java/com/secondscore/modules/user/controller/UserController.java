package com.secondscore.modules.user.controller;

import com.secondscore.common.api.ApiResponse;
import com.secondscore.modules.user.dto.UserCreateRequest;
import com.secondscore.modules.user.dto.UserRoleAssignRequest;
import com.secondscore.modules.user.dto.UserStatusUpdateRequest;
import com.secondscore.modules.user.dto.UserUpdateRequest;
import com.secondscore.modules.user.service.SysUserService;
import com.secondscore.modules.user.vo.RoleVO;
import com.secondscore.modules.user.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('user:manage')")
public class UserController {

    private final SysUserService sysUserService;

    @GetMapping
    public ApiResponse<List<UserVO>> list(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(sysUserService.listUsers(keyword));
    }

    @GetMapping("/roles")
    public ApiResponse<List<RoleVO>> listRoles() {
        return ApiResponse.success(sysUserService.listRoles());
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(sysUserService.createUser(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        sysUserService.updateUser(id, request);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody UserStatusUpdateRequest request) {
        sysUserService.updateStatus(id, request.getStatus());
        return ApiResponse.success();
    }

    @PutMapping("/{id}/roles")
    public ApiResponse<Void> assignRoles(@PathVariable Long id, @Valid @RequestBody UserRoleAssignRequest request) {
        sysUserService.assignRoles(id, request.getRoleIds());
        return ApiResponse.success();
    }
}
