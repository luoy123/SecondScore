package com.secondscore.modules.signup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.secondscore.common.constants.PermissionCodes;
import com.secondscore.common.enums.ActivityStatus;
import com.secondscore.common.enums.AttendanceStatus;
import com.secondscore.common.enums.ReviewStatus;
import com.secondscore.common.enums.SignupStatus;
import com.secondscore.common.exception.BusinessException;
import com.secondscore.modules.activity.entity.Activity;
import com.secondscore.modules.activity.mapper.ActivityMapper;
import com.secondscore.modules.signup.dto.ParticipationMarkItem;
import com.secondscore.modules.signup.dto.ParticipationMarkRequest;
import com.secondscore.modules.signup.entity.ActivityParticipation;
import com.secondscore.modules.signup.entity.ActivitySignup;
import com.secondscore.modules.signup.mapper.ActivityParticipationMapper;
import com.secondscore.modules.signup.mapper.ActivitySignupMapper;
import com.secondscore.modules.signup.service.SignupService;
import com.secondscore.modules.signup.vo.ParticipationVO;
import com.secondscore.modules.signup.vo.SignupVO;
import com.secondscore.modules.user.entity.SysUser;
import com.secondscore.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {

    private final ActivityMapper activityMapper;
    private final ActivitySignupMapper activitySignupMapper;
    private final ActivityParticipationMapper activityParticipationMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public void signup(Long activityId, Long studentId) {
        Activity activity = requireActivity(activityId);
        validateCanSignup(activity);

        Long signupCount = activitySignupMapper.countActiveSignup(activityId);
        if (signupCount != null && signupCount >= activity.getCapacity()) {
            throw new BusinessException("活动报名人数已满");
        }

        ActivitySignup existed = activitySignupMapper.selectOne(new LambdaQueryWrapper<ActivitySignup>()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getStudentId, studentId));
        if (existed == null) {
            ActivitySignup signup = new ActivitySignup();
            signup.setActivityId(activityId);
            signup.setStudentId(studentId);
            signup.setApplyTime(LocalDateTime.now());
            signup.setReviewStatus(ReviewStatus.PENDING.name());
            signup.setSignupStatus(SignupStatus.ACTIVE.name());
            activitySignupMapper.insert(signup);
            return;
        }

        if (SignupStatus.CANCELLED.name().equals(existed.getSignupStatus())) {
            ActivitySignup update = new ActivitySignup();
            update.setId(existed.getId());
            update.setApplyTime(LocalDateTime.now());
            update.setReviewStatus(ReviewStatus.PENDING.name());
            update.setReviewBy(null);
            update.setReviewTime(null);
            update.setRejectReason(null);
            update.setSignupStatus(SignupStatus.ACTIVE.name());
            activitySignupMapper.updateById(update);
            return;
        }

        throw new BusinessException("请勿重复报名");
    }

    @Override
    public void cancelSignup(Long activityId, Long studentId) {
        Activity activity = requireActivity(activityId);
        if (LocalDateTime.now().isAfter(activity.getSignupEnd())) {
            throw new BusinessException("报名截止后不能取消报名");
        }

        ActivitySignup signup = activitySignupMapper.selectOne(new LambdaQueryWrapper<ActivitySignup>()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getStudentId, studentId)
                .eq(ActivitySignup::getSignupStatus, SignupStatus.ACTIVE.name()));
        if (signup == null) {
            throw new BusinessException("未找到有效报名记录");
        }
        ActivitySignup update = new ActivitySignup();
        update.setId(signup.getId());
        update.setSignupStatus(SignupStatus.CANCELLED.name());
        activitySignupMapper.updateById(update);
    }

    @Override
    public List<SignupVO> listMySignups(Long studentId) {
        List<ActivitySignup> signups = activitySignupMapper.selectList(new LambdaQueryWrapper<ActivitySignup>()
                .eq(ActivitySignup::getStudentId, studentId)
                .orderByDesc(ActivitySignup::getApplyTime));
        List<SignupVO> result = new ArrayList<>();
        for (ActivitySignup signup : signups) {
            result.add(toSignupVO(signup));
        }
        return result;
    }

    @Override
    public List<SignupVO> listSignups(Long activityId, Long currentUserId, List<String> permissionCodes) {
        Activity activity = requireActivity(activityId);
        checkManagePermission(activity, currentUserId, permissionCodes,
                PermissionCodes.SIGNUP_REVIEW_OWN,
                PermissionCodes.SIGNUP_REVIEW_ALL,
                "无权限操作该活动报名数据");
        List<ActivitySignup> signups = activitySignupMapper.selectList(new LambdaQueryWrapper<ActivitySignup>()
                .eq(ActivitySignup::getActivityId, activityId)
                .orderByDesc(ActivitySignup::getApplyTime));
        List<SignupVO> result = new ArrayList<>();
        for (ActivitySignup signup : signups) {
            result.add(toSignupVO(signup));
        }
        return result;
    }

    @Override
    public void approveSignup(Long signupId, Long reviewerId, List<String> permissionCodes) {
        ActivitySignup signup = requireSignup(signupId);
        Activity activity = requireActivity(signup.getActivityId());
        checkManagePermission(activity, reviewerId, permissionCodes,
                PermissionCodes.SIGNUP_REVIEW_OWN,
                PermissionCodes.SIGNUP_REVIEW_ALL,
                "无权限操作该活动报名数据");
        if (!ReviewStatus.PENDING.name().equals(signup.getReviewStatus())) {
            throw new BusinessException("仅待审核状态可操作");
        }
        ActivitySignup update = new ActivitySignup();
        update.setId(signupId);
        update.setReviewStatus(ReviewStatus.APPROVED.name());
        update.setReviewBy(reviewerId);
        update.setReviewTime(LocalDateTime.now());
        update.setRejectReason(null);
        activitySignupMapper.updateById(update);
    }

    @Override
    public void rejectSignup(Long signupId, String reason, Long reviewerId, List<String> permissionCodes) {
        ActivitySignup signup = requireSignup(signupId);
        Activity activity = requireActivity(signup.getActivityId());
        checkManagePermission(activity, reviewerId, permissionCodes,
                PermissionCodes.SIGNUP_REVIEW_OWN,
                PermissionCodes.SIGNUP_REVIEW_ALL,
                "无权限操作该活动报名数据");
        if (!ReviewStatus.PENDING.name().equals(signup.getReviewStatus())) {
            throw new BusinessException("仅待审核状态可操作");
        }
        ActivitySignup update = new ActivitySignup();
        update.setId(signupId);
        update.setReviewStatus(ReviewStatus.REJECTED.name());
        update.setReviewBy(reviewerId);
        update.setReviewTime(LocalDateTime.now());
        update.setRejectReason(reason);
        activitySignupMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markParticipation(Long activityId, ParticipationMarkRequest request, Long currentUserId, List<String> permissionCodes) {
        Activity activity = requireActivity(activityId);
        checkManagePermission(activity, currentUserId, permissionCodes,
                PermissionCodes.PARTICIPATION_MARK_OWN,
                PermissionCodes.PARTICIPATION_MARK_ALL,
                "无权限操作该活动参与登记");
        if (LocalDateTime.now().isBefore(activity.getEndTime())) {
            throw new BusinessException("活动未结束，暂不能登记参与情况");
        }

        for (ParticipationMarkItem item : request.getItems()) {
            if (!AttendanceStatus.PRESENT.name().equals(item.getAttendanceStatus())
                    && !AttendanceStatus.ABSENT.name().equals(item.getAttendanceStatus())) {
                throw new BusinessException("attendanceStatus 仅支持 PRESENT/ABSENT");
            }

            ActivitySignup signup = activitySignupMapper.selectOne(new LambdaQueryWrapper<ActivitySignup>()
                    .eq(ActivitySignup::getActivityId, activityId)
                    .eq(ActivitySignup::getStudentId, item.getStudentId())
                    .eq(ActivitySignup::getSignupStatus, SignupStatus.ACTIVE.name()));
            if (signup == null || !ReviewStatus.APPROVED.name().equals(signup.getReviewStatus())) {
                throw new BusinessException("学生未通过报名审核，无法登记参与: " + item.getStudentId());
            }

            ActivityParticipation existed = activityParticipationMapper.selectOne(new LambdaQueryWrapper<ActivityParticipation>()
                    .eq(ActivityParticipation::getActivityId, activityId)
                    .eq(ActivityParticipation::getStudentId, item.getStudentId()));
            if (existed == null) {
                ActivityParticipation insert = new ActivityParticipation();
                insert.setActivityId(activityId);
                insert.setStudentId(item.getStudentId());
                insert.setAttendanceStatus(item.getAttendanceStatus());
                insert.setMarkedBy(currentUserId);
                insert.setMarkedTime(LocalDateTime.now());
                insert.setRemark(item.getRemark());
                activityParticipationMapper.insert(insert);
            } else {
                ActivityParticipation update = new ActivityParticipation();
                update.setId(existed.getId());
                update.setAttendanceStatus(item.getAttendanceStatus());
                update.setMarkedBy(currentUserId);
                update.setMarkedTime(LocalDateTime.now());
                update.setRemark(item.getRemark());
                activityParticipationMapper.updateById(update);
            }
        }
    }

    @Override
    public List<ParticipationVO> listParticipation(Long activityId, Long currentUserId, List<String> permissionCodes) {
        Activity activity = requireActivity(activityId);
        checkManagePermission(activity, currentUserId, permissionCodes,
                PermissionCodes.PARTICIPATION_MARK_OWN,
                PermissionCodes.PARTICIPATION_MARK_ALL,
                "无权限操作该活动参与登记");
        List<ActivityParticipation> participations = activityParticipationMapper.selectList(new LambdaQueryWrapper<ActivityParticipation>()
                .eq(ActivityParticipation::getActivityId, activityId)
                .orderByAsc(ActivityParticipation::getStudentId));
        List<ParticipationVO> result = new ArrayList<>();
        for (ActivityParticipation participation : participations) {
            result.add(toParticipationVO(participation));
        }
        return result;
    }

    private void validateCanSignup(Activity activity) {
        if (!ActivityStatus.PUBLISHED.name().equals(activity.getStatus())) {
            throw new BusinessException("当前活动不可报名");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getSignupStart()) || now.isAfter(activity.getSignupEnd())) {
            throw new BusinessException("当前不在报名时间范围内");
        }
    }

    private void checkManagePermission(Activity activity,
                                       Long currentUserId,
                                       List<String> permissionCodes,
                                       String ownPermission,
                                       String allPermission,
                                       String denyMessage) {
        if (hasPermission(permissionCodes, allPermission)) {
            return;
        }
        if (hasPermission(permissionCodes, ownPermission) && activity.getOrganizerId().equals(currentUserId)) {
            return;
        }
        throw new BusinessException(403, denyMessage);
    }

    private boolean hasPermission(List<String> permissions, String code) {
        return permissions != null && permissions.contains(code);
    }

    private Activity requireActivity(Long activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }
        return activity;
    }

    private ActivitySignup requireSignup(Long signupId) {
        ActivitySignup signup = activitySignupMapper.selectById(signupId);
        if (signup == null) {
            throw new BusinessException(404, "报名记录不存在");
        }
        return signup;
    }

    private SignupVO toSignupVO(ActivitySignup signup) {
        SignupVO vo = new SignupVO();
        vo.setId(signup.getId());
        vo.setActivityId(signup.getActivityId());
        vo.setStudentId(signup.getStudentId());
        vo.setApplyTime(signup.getApplyTime());
        vo.setReviewStatus(signup.getReviewStatus());
        vo.setReviewBy(signup.getReviewBy());
        vo.setReviewTime(signup.getReviewTime());
        vo.setRejectReason(signup.getRejectReason());
        vo.setSignupStatus(signup.getSignupStatus());

        SysUser student = sysUserMapper.selectById(signup.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getRealName());
            vo.setStudentNo(student.getStudentNo());
        }
        if (signup.getReviewBy() != null) {
            SysUser reviewer = sysUserMapper.selectById(signup.getReviewBy());
            if (reviewer != null) {
                vo.setReviewerName(reviewer.getRealName());
            }
        }
        return vo;
    }

    private ParticipationVO toParticipationVO(ActivityParticipation participation) {
        ParticipationVO vo = new ParticipationVO();
        vo.setId(participation.getId());
        vo.setActivityId(participation.getActivityId());
        vo.setStudentId(participation.getStudentId());
        vo.setAttendanceStatus(participation.getAttendanceStatus());
        vo.setMarkedBy(participation.getMarkedBy());
        vo.setMarkedTime(participation.getMarkedTime());
        vo.setRemark(participation.getRemark());

        SysUser student = sysUserMapper.selectById(participation.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getRealName());
            vo.setStudentNo(student.getStudentNo());
        }
        SysUser marker = sysUserMapper.selectById(participation.getMarkedBy());
        if (marker != null) {
            vo.setMarkerName(marker.getRealName());
        }
        return vo;
    }
}
