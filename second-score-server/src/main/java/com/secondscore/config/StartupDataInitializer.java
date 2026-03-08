package com.secondscore.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.secondscore.common.constants.PermissionCodes;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupDataInitializer {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(rollbackFor = Exception.class)
    public void init() {
        Long studentRoleId = ensureRole("STUDENT", "学生");
        Long activityAdminRoleId = ensureRole("ACTIVITY_ADMIN", "活动管理员");
        Long systemAdminRoleId = ensureRole("SYSTEM_ADMIN", "系统管理员");

        Long pMenuStudentHall = ensurePermission(PermissionCodes.MENU_STUDENT_HALL, "菜单-活动大厅", "MENU");
        Long pMenuStudentSignups = ensurePermission(PermissionCodes.MENU_STUDENT_SIGNUPS, "菜单-我的报名", "MENU");
        Long pMenuStudentCredits = ensurePermission(PermissionCodes.MENU_STUDENT_CREDITS, "菜单-我的学分", "MENU");
        Long pMenuAdminAnalysis = ensurePermission(PermissionCodes.MENU_ADMIN_ANALYSIS, "菜单-数据分析", "MENU");
        Long pMenuAdminActivities = ensurePermission(PermissionCodes.MENU_ADMIN_ACTIVITIES, "菜单-活动管理", "MENU");
        Long pMenuAdminSignupReview = ensurePermission(PermissionCodes.MENU_ADMIN_SIGNUP_REVIEW, "菜单-报名审核", "MENU");
        Long pMenuAdminParticipation = ensurePermission(PermissionCodes.MENU_ADMIN_PARTICIPATION, "菜单-参与登记", "MENU");
        Long pMenuAdminCreditGrant = ensurePermission(PermissionCodes.MENU_ADMIN_CREDIT_GRANT, "菜单-学分发放", "MENU");
        Long pMenuSystemUsers = ensurePermission(PermissionCodes.MENU_SYSTEM_USERS, "菜单-用户管理", "MENU");
        Long pMenuSystemBase = ensurePermission(PermissionCodes.MENU_SYSTEM_BASE, "菜单-基础信息管理", "MENU");
        Long pMenuSystemRoles = ensurePermission(PermissionCodes.MENU_SYSTEM_ROLES, "菜单-角色权限", "MENU");

        Long pActivityView = ensurePermission(PermissionCodes.ACTIVITY_VIEW, "活动查看", "API");
        Long pActivityManageOwn = ensurePermission(PermissionCodes.ACTIVITY_MANAGE_OWN, "活动管理(本人)", "API");
        Long pActivityManageAll = ensurePermission(PermissionCodes.ACTIVITY_MANAGE_ALL, "活动管理(全部)", "API");
        Long pSignupApply = ensurePermission(PermissionCodes.SIGNUP_APPLY, "学生报名", "API");
        Long pSignupReviewOwn = ensurePermission(PermissionCodes.SIGNUP_REVIEW_OWN, "报名审核(本人)", "API");
        Long pSignupReviewAll = ensurePermission(PermissionCodes.SIGNUP_REVIEW_ALL, "报名审核(全部)", "API");
        Long pParticipationMarkOwn = ensurePermission(PermissionCodes.PARTICIPATION_MARK_OWN, "参与登记(本人)", "API");
        Long pParticipationMarkAll = ensurePermission(PermissionCodes.PARTICIPATION_MARK_ALL, "参与登记(全部)", "API");
        Long pCreditGrantOwn = ensurePermission(PermissionCodes.CREDIT_GRANT_OWN, "学分发放(本人)", "API");
        Long pCreditGrantAll = ensurePermission(PermissionCodes.CREDIT_GRANT_ALL, "学分发放(全部)", "API");
        Long pCreditRevokeOwn = ensurePermission(PermissionCodes.CREDIT_REVOKE_OWN, "学分撤销(本人)", "API");
        Long pCreditRevokeAll = ensurePermission(PermissionCodes.CREDIT_REVOKE_ALL, "学分撤销(全部)", "API");
        Long pCreditViewSelf = ensurePermission(PermissionCodes.CREDIT_VIEW_SELF, "学分查看(本人)", "API");
        Long pCreditViewOwn = ensurePermission(PermissionCodes.CREDIT_VIEW_OWN, "学分查看(本人活动)", "API");
        Long pCreditViewAll = ensurePermission(PermissionCodes.CREDIT_VIEW_ALL, "学分查看(全部)", "API");
        Long pAnalysisView = ensurePermission(PermissionCodes.ANALYSIS_VIEW, "数据分析查看", "API");
        Long pUserManage = ensurePermission(PermissionCodes.USER_MANAGE, "用户管理", "API");
        Long pBaseManage = ensurePermission(PermissionCodes.BASE_MANAGE, "基础信息管理", "API");
        Long pRoleManage = ensurePermission(PermissionCodes.ROLE_MANAGE, "角色权限管理", "API");

        // 学生默认权限
        ensureRolePermission(studentRoleId, pMenuStudentHall);
        ensureRolePermission(studentRoleId, pMenuStudentSignups);
        ensureRolePermission(studentRoleId, pMenuStudentCredits);
        ensureRolePermission(studentRoleId, pActivityView);
        ensureRolePermission(studentRoleId, pSignupApply);
        ensureRolePermission(studentRoleId, pCreditViewSelf);

        // 活动管理员默认权限
        ensureRolePermission(activityAdminRoleId, pMenuAdminAnalysis);
        ensureRolePermission(activityAdminRoleId, pMenuAdminActivities);
        ensureRolePermission(activityAdminRoleId, pMenuAdminSignupReview);
        ensureRolePermission(activityAdminRoleId, pMenuAdminParticipation);
        ensureRolePermission(activityAdminRoleId, pMenuAdminCreditGrant);
        ensureRolePermission(activityAdminRoleId, pActivityView);
        ensureRolePermission(activityAdminRoleId, pActivityManageOwn);
        ensureRolePermission(activityAdminRoleId, pSignupReviewOwn);
        ensureRolePermission(activityAdminRoleId, pParticipationMarkOwn);
        ensureRolePermission(activityAdminRoleId, pCreditGrantOwn);
        ensureRolePermission(activityAdminRoleId, pCreditRevokeOwn);
        ensureRolePermission(activityAdminRoleId, pCreditViewOwn);
        ensureRolePermission(activityAdminRoleId, pAnalysisView);

        // 系统管理员默认权限
        ensureRolePermission(systemAdminRoleId, pMenuAdminAnalysis);
        ensureRolePermission(systemAdminRoleId, pMenuAdminActivities);
        ensureRolePermission(systemAdminRoleId, pMenuAdminSignupReview);
        ensureRolePermission(systemAdminRoleId, pMenuAdminParticipation);
        ensureRolePermission(systemAdminRoleId, pMenuAdminCreditGrant);
        ensureRolePermission(systemAdminRoleId, pMenuSystemUsers);
        ensureRolePermission(systemAdminRoleId, pMenuSystemBase);
        ensureRolePermission(systemAdminRoleId, pMenuSystemRoles);
        ensureRolePermission(systemAdminRoleId, pActivityView);
        ensureRolePermission(systemAdminRoleId, pActivityManageAll);
        ensureRolePermission(systemAdminRoleId, pSignupReviewAll);
        ensureRolePermission(systemAdminRoleId, pParticipationMarkAll);
        ensureRolePermission(systemAdminRoleId, pCreditGrantAll);
        ensureRolePermission(systemAdminRoleId, pCreditRevokeAll);
        ensureRolePermission(systemAdminRoleId, pCreditViewAll);
        ensureRolePermission(systemAdminRoleId, pAnalysisView);
        ensureRolePermission(systemAdminRoleId, pUserManage);
        ensureRolePermission(systemAdminRoleId, pBaseManage);
        ensureRolePermission(systemAdminRoleId, pRoleManage);

        Long studentId = ensureUser("stu01", "123456", "张三", "20230001");
        Long activityAdminId = ensureUser("actadmin", "123456", "李老师", null);
        Long adminId = ensureUser("admin", "123456", "系统管理员", null);

        ensureUserRole(studentId, studentRoleId);
        ensureUserRole(activityAdminId, activityAdminRoleId);
        ensureUserRole(adminId, systemAdminRoleId);

        encodePlainTextPasswordIfNeeded();
        log.info("默认角色、权限与账号初始化完成");
    }

    private Long ensureRole(String code, String name) {
        SysRole role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, code));
        if (role != null) {
            return role.getId();
        }
        SysRole insert = new SysRole();
        insert.setRoleCode(code);
        insert.setRoleName(name);
        insert.setStatus(1);
        sysRoleMapper.insert(insert);
        return insert.getId();
    }

    private Long ensurePermission(String code, String name, String type) {
        SysPermission permission = sysPermissionMapper.selectOne(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getPermCode, code));
        if (permission != null) {
            return permission.getId();
        }
        SysPermission insert = new SysPermission();
        insert.setPermCode(code);
        insert.setPermName(name);
        insert.setPermType(type);
        insert.setStatus(1);
        sysPermissionMapper.insert(insert);
        return insert.getId();
    }

    private Long ensureUser(String username, String rawPassword, String realName, String studentNo) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user != null) {
            return user.getId();
        }
        SysUser insert = new SysUser();
        insert.setUsername(username);
        insert.setPasswordHash(passwordEncoder.encode(rawPassword));
        insert.setRealName(realName);
        insert.setStudentNo(studentNo);
        insert.setStatus(1);
        sysUserMapper.insert(insert);
        return insert.getId();
    }

    private void ensureUserRole(Long userId, Long roleId) {
        SysUserRole userRole = sysUserRoleMapper.selectOne(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getRoleId, roleId));
        if (userRole != null) {
            return;
        }
        SysUserRole insert = new SysUserRole();
        insert.setUserId(userId);
        insert.setRoleId(roleId);
        sysUserRoleMapper.insert(insert);
    }

    private void ensureRolePermission(Long roleId, Long permissionId) {
        SysRolePermission rp = sysRolePermissionMapper.selectOne(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId)
                .eq(SysRolePermission::getPermissionId, permissionId));
        if (rp != null) {
            return;
        }
        SysRolePermission insert = new SysRolePermission();
        insert.setRoleId(roleId);
        insert.setPermissionId(permissionId);
        sysRolePermissionMapper.insert(insert);
    }

    private void encodePlainTextPasswordIfNeeded() {
        List<SysUser> users = sysUserMapper.selectList(null);
        for (SysUser user : users) {
            String password = user.getPasswordHash();
            if (password == null) {
                continue;
            }
            if (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$")) {
                continue;
            }
            SysUser update = new SysUser();
            update.setId(user.getId());
            update.setPasswordHash(passwordEncoder.encode(password));
            sysUserMapper.updateById(update);
        }
    }
}
