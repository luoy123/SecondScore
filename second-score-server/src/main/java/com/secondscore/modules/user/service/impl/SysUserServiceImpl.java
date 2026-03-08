package com.secondscore.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.secondscore.common.exception.BusinessException;
import com.secondscore.modules.user.dto.RoleSaveRequest;
import com.secondscore.modules.user.dto.UserCreateRequest;
import com.secondscore.modules.user.dto.UserUpdateRequest;
import com.secondscore.modules.user.entity.SysPermission;
import com.secondscore.modules.user.entity.SysRole;
import com.secondscore.modules.user.entity.SysRolePermission;
import com.secondscore.modules.user.entity.SysUser;
import com.secondscore.modules.user.entity.SysUserRole;
import com.secondscore.modules.user.mapper.SysPermissionMapper;
import com.secondscore.modules.user.mapper.SysRoleMapper;
import com.secondscore.modules.user.mapper.SysRolePermissionMapper;
import com.secondscore.modules.user.mapper.SysUserMapper;
import com.secondscore.modules.user.mapper.SysUserRoleMapper;
import com.secondscore.modules.user.service.SysUserService;
import com.secondscore.modules.user.vo.PermissionVO;
import com.secondscore.modules.user.vo.RoleVO;
import com.secondscore.modules.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.upload.base-dir:uploads}")
    private String uploadBaseDir;
    @Value("${app.upload.avatar-sub-dir:avatar}")
    private String avatarSubDir;

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
    }

    @Override
    public SysUser getById(Long userId) {
        return sysUserMapper.selectById(userId);
    }

    @Override
    public List<String> getRoleCodes(Long userId) {
        return sysRoleMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    public List<String> getPermissionCodes(Long userId) {
        return sysPermissionMapper.selectPermissionCodesByUserId(userId);
    }

    @Override
    public UserVO getCurrentUserProfile(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return toVO(user, getRoleCodes(user.getId()), getPermissionCodes(user.getId()));
    }

    @Override
    public List<UserVO> listUsers(String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getRealName, keyword)
                    .or()
                    .like(SysUser::getStudentNo, keyword);
        }
        List<SysUser> users = sysUserMapper.selectList(wrapper);
        List<UserVO> result = new ArrayList<>();
        for (SysUser user : users) {
            result.add(toVO(user, getRoleCodes(user.getId()), getPermissionCodes(user.getId())));
        }
        return result;
    }

    @Override
    public List<RoleVO> listRoles() {
        List<SysRole> roles = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .orderByAsc(SysRole::getId));
        List<RoleVO> result = new ArrayList<>();
        for (SysRole role : roles) {
            result.add(toRoleVO(role));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserCreateRequest request) {
        if (getByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setStudentNo(request.getStudentNo());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setCollegeId(request.getCollegeId());
        user.setMajorId(request.getMajorId());
        user.setClassId(request.getClassId());
        user.setStatus(1);
        sysUserMapper.insert(user);
        return user.getId();
    }

    @Override
    public void updateUser(Long userId, UserUpdateRequest request) {
        SysUser existed = getById(userId);
        if (existed == null) {
            throw new BusinessException(404, "用户不存在");
        }
        SysUser update = new SysUser();
        update.setId(userId);
        update.setRealName(request.getRealName());
        update.setPhone(request.getPhone());
        update.setEmail(request.getEmail());
        update.setCollegeId(request.getCollegeId());
        update.setMajorId(request.getMajorId());
        update.setClassId(request.getClassId());
        sysUserMapper.updateById(update);
    }

    @Override
    public void updateStatus(Long userId, Integer status) {
        SysUser existed = getById(userId);
        if (existed == null) {
            throw new BusinessException(404, "用户不存在");
        }
        SysUser update = new SysUser();
        update.setId(userId);
        update.setStatus(status);
        sysUserMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        SysUser existed = getById(userId);
        if (existed == null) {
            throw new BusinessException(404, "用户不存在");
        }
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        Set<Long> uniqueRoleIds = new LinkedHashSet<>(roleIds);
        for (Long roleId : uniqueRoleIds) {
            SysRole role = sysRoleMapper.selectById(roleId);
            if (role == null) {
                throw new BusinessException("角色不存在: " + roleId);
            }
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            sysUserRoleMapper.insert(ur);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleSaveRequest request) {
        String roleCode = request.getRoleCode().trim().toUpperCase();
        SysRole existed = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, roleCode));
        if (existed != null) {
            throw new BusinessException("角色编码已存在");
        }
        SysRole role = new SysRole();
        role.setRoleCode(roleCode);
        role.setRoleName(request.getRoleName().trim());
        role.setStatus(request.getStatus());
        sysRoleMapper.insert(role);
        return role.getId();
    }

    @Override
    public void updateRole(Long roleId, RoleSaveRequest request) {
        SysRole existed = sysRoleMapper.selectById(roleId);
        if (existed == null) {
            throw new BusinessException(404, "角色不存在");
        }
        String roleCode = request.getRoleCode().trim().toUpperCase();
        SysRole duplicate = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, roleCode)
                .ne(SysRole::getId, roleId));
        if (duplicate != null) {
            throw new BusinessException("角色编码已存在");
        }
        SysRole update = new SysRole();
        update.setId(roleId);
        update.setRoleCode(roleCode);
        update.setRoleName(request.getRoleName().trim());
        update.setStatus(request.getStatus());
        sysRoleMapper.updateById(update);
    }

    @Override
    public List<PermissionVO> listPermissions() {
        List<SysPermission> permissions = sysPermissionMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                .orderByAsc(SysPermission::getPermType)
                .orderByAsc(SysPermission::getId));
        List<PermissionVO> result = new ArrayList<>();
        for (SysPermission permission : permissions) {
            PermissionVO vo = new PermissionVO();
            vo.setId(permission.getId());
            vo.setPermCode(permission.getPermCode());
            vo.setPermName(permission.getPermName());
            vo.setPermType(permission.getPermType());
            vo.setStatus(permission.getStatus());
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(404, "角色不存在");
        }
        return sysPermissionMapper.selectPermissionIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolePermissions(Long roleId, List<Long> permissionIds) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(404, "角色不存在");
        }
        sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId));
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        Set<Long> uniquePermissionIds = new LinkedHashSet<>(permissionIds);
        for (Long permissionId : uniquePermissionIds) {
            SysPermission permission = sysPermissionMapper.selectById(permissionId);
            if (permission == null) {
                throw new BusinessException("权限不存在: " + permissionId);
            }
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            sysRolePermissionMapper.insert(rp);
        }
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessException("旧密码错误");
        }
        SysUser update = new SysUser();
        update.setId(userId);
        update.setPasswordHash(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(update);
    }

    @Override
    public String updateAvatar(Long userId, MultipartFile file) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "头像文件不能为空");
        }
        if (file.getSize() > 3 * 1024 * 1024) {
            throw new BusinessException(400, "头像文件不能超过3MB");
        }
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !contentType.startsWith("image/")) {
            throw new BusinessException(400, "仅支持图片文件");
        }

        Path avatarDir = Paths.get(uploadBaseDir, avatarSubDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(avatarDir);
            String ext = resolveExt(file.getOriginalFilename(), contentType);
            String fileName = "u" + userId + "_" + UUID.randomUUID().toString().replace("-", "") + ext;
            Path avatarPath = avatarDir.resolve(fileName);
            file.transferTo(avatarPath.toFile());

            String avatarUrl = "/uploads/" + avatarSubDir + "/" + fileName;
            SysUser update = new SysUser();
            update.setId(userId);
            update.setAvatarUrl(avatarUrl);
            sysUserMapper.updateById(update);
            return avatarUrl;
        } catch (IOException ex) {
            throw new BusinessException(500, "头像上传失败: " + ex.getMessage());
        }
    }

    private UserVO toVO(SysUser user, List<String> roleCodes, List<String> permissionCodes) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setStudentNo(user.getStudentNo());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setCollegeId(user.getCollegeId());
        vo.setMajorId(user.getMajorId());
        vo.setClassId(user.getClassId());
        vo.setStatus(user.getStatus());
        vo.setRoles(roleCodes);
        vo.setPermissions(permissionCodes);
        return vo;
    }

    private RoleVO toRoleVO(SysRole role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setRoleCode(role.getRoleCode());
        vo.setRoleName(role.getRoleName());
        vo.setStatus(role.getStatus());
        return vo;
    }

    private String resolveExt(String originalFilename, String contentType) {
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
            if (".jpg".equals(ext) || ".jpeg".equals(ext) || ".png".equals(ext) || ".webp".equals(ext) || ".gif".equals(ext)) {
                return ext;
            }
        }
        if ("image/jpeg".equalsIgnoreCase(contentType)) {
            return ".jpg";
        }
        if ("image/png".equalsIgnoreCase(contentType)) {
            return ".png";
        }
        if ("image/webp".equalsIgnoreCase(contentType)) {
            return ".webp";
        }
        if ("image/gif".equalsIgnoreCase(contentType)) {
            return ".gif";
        }
        return ".png";
    }
}
