package com.secondscore.modules.user.service;

import com.secondscore.modules.user.dto.UserCreateRequest;
import com.secondscore.modules.user.dto.RoleSaveRequest;
import com.secondscore.modules.user.dto.ProfileUpdateRequest;
import com.secondscore.modules.user.dto.UserUpdateRequest;
import com.secondscore.modules.user.entity.SysUser;
import com.secondscore.modules.user.vo.PermissionVO;
import com.secondscore.modules.user.vo.RoleVO;
import com.secondscore.modules.user.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SysUserService {
    SysUser getByUsername(String username);

    SysUser getById(Long userId);

    List<String> getRoleCodes(Long userId);

    List<String> getPermissionCodes(Long userId);

    UserVO getCurrentUserProfile(Long userId);

    List<UserVO> listUsers(String keyword, String roleCode, Integer status);

    List<RoleVO> listRoles();

    Long createUser(UserCreateRequest request);

    void updateUser(Long userId, UserUpdateRequest request);

    void updateStatus(Long userId, Integer status);

    void assignRoles(Long userId, List<Long> roleIds);

    void updateSelfProfile(Long userId, ProfileUpdateRequest request);

    Long createRole(RoleSaveRequest request);

    void updateRole(Long roleId, RoleSaveRequest request);

    List<PermissionVO> listPermissions();

    List<Long> getRolePermissionIds(Long roleId);

    void assignRolePermissions(Long roleId, List<Long> permissionIds);

    void changePassword(Long userId, String oldPassword, String newPassword);

    String updateAvatar(Long userId, MultipartFile file);
}
