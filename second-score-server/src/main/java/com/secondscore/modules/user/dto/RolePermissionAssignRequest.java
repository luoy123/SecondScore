package com.secondscore.modules.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class RolePermissionAssignRequest {
    private List<Long> permissionIds;
}
