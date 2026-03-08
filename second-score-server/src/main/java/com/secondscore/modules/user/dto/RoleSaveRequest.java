package com.secondscore.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RoleSaveRequest {
    @NotBlank(message = "角色编码不能为空")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]{1,30}$", message = "角色编码仅支持大写字母/数字/下划线，且以字母开头")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
