package com.secondscore.common.security;

import com.secondscore.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            throw new BusinessException(401, "未登录");
        }
        return loginUser.getUserId();
    }

    public static List<String> getCurrentRoleCodes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            throw new BusinessException(401, "未登录");
        }
        return loginUser.getRoles();
    }

    public static boolean hasRole(String roleCode) {
        return getCurrentRoleCodes().contains(roleCode);
    }

    public static List<String> getCurrentPermissionCodes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            throw new BusinessException(401, "未登录");
        }
        return loginUser.getPermissions();
    }

    public static boolean hasPermission(String permissionCode) {
        return getCurrentPermissionCodes().contains(permissionCode);
    }

    public static boolean hasAnyPermission(String... permissionCodes) {
        List<String> owned = getCurrentPermissionCodes();
        for (String permissionCode : permissionCodes) {
            if (owned.contains(permissionCode)) {
                return true;
            }
        }
        return false;
    }
}
