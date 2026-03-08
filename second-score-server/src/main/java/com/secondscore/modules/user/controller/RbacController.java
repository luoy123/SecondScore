package com.secondscore.modules.user.controller;

import com.secondscore.common.api.ApiResponse;
import com.secondscore.modules.user.dto.RolePermissionAssignRequest;
import com.secondscore.modules.user.dto.RoleSaveRequest;
import com.secondscore.modules.user.service.SysUserService;
import com.secondscore.modules.user.vo.PermissionVO;
import com.secondscore.modules.user.vo.RoleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('role:manage')")
public class RbacController {

    private final SysUserService sysUserService;

    @GetMapping("/roles")
    public ApiResponse<List<RoleVO>> listRoles() {
        return ApiResponse.success(sysUserService.listRoles());
    }

    @PostMapping("/roles")
    public ApiResponse<Long> createRole(@Valid @RequestBody RoleSaveRequest request) {
        return ApiResponse.success(sysUserService.createRole(request));
    }

    @PutMapping("/roles/{id}")
    public ApiResponse<Void> updateRole(@PathVariable Long id, @Valid @RequestBody RoleSaveRequest request) {
        sysUserService.updateRole(id, request);
        return ApiResponse.success();
    }

    @GetMapping("/permissions")
    public ApiResponse<List<PermissionVO>> listPermissions() {
        return ApiResponse.success(sysUserService.listPermissions());
    }

    @GetMapping("/roles/{id}/permissions")
    public ApiResponse<List<Long>> listRolePermissions(@PathVariable Long id) {
        return ApiResponse.success(sysUserService.getRolePermissionIds(id));
    }

    @PutMapping("/roles/{id}/permissions")
    public ApiResponse<Void> assignRolePermissions(@PathVariable Long id,
                                                   @RequestBody RolePermissionAssignRequest request) {
        sysUserService.assignRolePermissions(id, request.getPermissionIds());
        return ApiResponse.success();
    }
}
