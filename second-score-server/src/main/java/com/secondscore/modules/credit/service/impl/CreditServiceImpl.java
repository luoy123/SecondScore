package com.secondscore.modules.credit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.secondscore.common.constants.PermissionCodes;
import com.secondscore.common.enums.AttendanceStatus;
import com.secondscore.common.enums.GrantStatus;
import com.secondscore.common.enums.ReviewStatus;
import com.secondscore.common.enums.SignupStatus;
import com.secondscore.common.exception.BusinessException;
import com.secondscore.modules.activity.entity.Activity;
import com.secondscore.modules.activity.mapper.ActivityMapper;
import com.secondscore.modules.base.entity.BaseActivityCategory;
import com.secondscore.modules.base.entity.BaseTerm;
import com.secondscore.modules.base.mapper.BaseActivityCategoryMapper;
import com.secondscore.modules.base.mapper.BaseTermMapper;
import com.secondscore.modules.credit.entity.CreditRecord;
import com.secondscore.modules.credit.mapper.CreditRecordMapper;
import com.secondscore.modules.credit.service.CreditService;
import com.secondscore.modules.credit.vo.CreditGrantResultVO;
import com.secondscore.modules.credit.vo.CreditRecordVO;
import com.secondscore.modules.credit.vo.PersonalCreditSummaryVO;
import com.secondscore.modules.signup.entity.ActivityParticipation;
import com.secondscore.modules.signup.entity.ActivitySignup;
import com.secondscore.modules.signup.mapper.ActivityParticipationMapper;
import com.secondscore.modules.signup.mapper.ActivitySignupMapper;
import com.secondscore.modules.user.entity.SysUser;
import com.secondscore.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final CreditRecordMapper creditRecordMapper;
    private final ActivityMapper activityMapper;
    private final ActivitySignupMapper activitySignupMapper;
    private final ActivityParticipationMapper activityParticipationMapper;
    private final BaseActivityCategoryMapper baseActivityCategoryMapper;
    private final BaseTermMapper baseTermMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreditGrantResultVO grantByActivity(Long activityId, Long currentUserId, List<String> permissionCodes) {
        Activity activity = requireActivity(activityId);
        checkManagePermission(activity, currentUserId, permissionCodes,
                PermissionCodes.CREDIT_GRANT_OWN,
                PermissionCodes.CREDIT_GRANT_ALL,
                "无权限操作该活动学分");
        if (LocalDateTime.now().isBefore(activity.getEndTime())) {
            throw new BusinessException("活动未结束，不能发放学分");
        }

        BaseActivityCategory category = requireCategory(activity.getCategoryId());
        CreditGrantResultVO result = new CreditGrantResultVO();

        List<ActivityParticipation> participations = activityParticipationMapper.selectList(new LambdaQueryWrapper<ActivityParticipation>()
                .eq(ActivityParticipation::getActivityId, activityId)
                .eq(ActivityParticipation::getAttendanceStatus, AttendanceStatus.PRESENT.name()));

        for (ActivityParticipation participation : participations) {
            Long studentId = participation.getStudentId();
            SysUser student = sysUserMapper.selectById(studentId);
            String studentName = student == null ? String.valueOf(studentId) : student.getRealName();

            ActivitySignup signup = activitySignupMapper.selectOne(new LambdaQueryWrapper<ActivitySignup>()
                    .eq(ActivitySignup::getActivityId, activityId)
                    .eq(ActivitySignup::getStudentId, studentId)
                    .eq(ActivitySignup::getSignupStatus, SignupStatus.ACTIVE.name()));
            if (signup == null || !ReviewStatus.APPROVED.name().equals(signup.getReviewStatus())) {
                result.setSkippedCount(result.getSkippedCount() + 1);
                result.getMessages().add("跳过 " + studentName + ": 未通过报名审核");
                continue;
            }

            CreditRecord existed = creditRecordMapper.selectOne(new LambdaQueryWrapper<CreditRecord>()
                    .eq(CreditRecord::getActivityId, activityId)
                    .eq(CreditRecord::getStudentId, studentId));
            if (existed != null) {
                result.setSkippedCount(result.getSkippedCount() + 1);
                result.getMessages().add("跳过 " + studentName + ": 已存在学分记录");
                continue;
            }

            BigDecimal currentCategoryCredit = creditRecordMapper.sumGrantedCreditByTermAndCategory(
                    studentId, activity.getTermId(), activity.getCategoryId());
            BigDecimal afterGrant = currentCategoryCredit.add(activity.getCredit());
            if (afterGrant.compareTo(category.getMaxCreditPerTerm()) > 0) {
                result.setSkippedCount(result.getSkippedCount() + 1);
                result.getMessages().add("跳过 " + studentName + ": 超出类别学期学分上限");
                continue;
            }

            CreditRecord record = new CreditRecord();
            record.setStudentId(studentId);
            record.setActivityId(activityId);
            record.setTermId(activity.getTermId());
            record.setCategoryId(activity.getCategoryId());
            record.setCredit(activity.getCredit());
            record.setGrantStatus(GrantStatus.GRANTED.name());
            record.setGrantBy(currentUserId);
            record.setGrantTime(LocalDateTime.now());
            creditRecordMapper.insert(record);

            result.setGrantedCount(result.getGrantedCount() + 1);
            result.getMessages().add("成功发放 " + studentName + " 学分: " + activity.getCredit());
        }
        return result;
    }

    @Override
    public void revoke(Long creditRecordId, String reason, Long currentUserId, List<String> permissionCodes) {
        CreditRecord record = requireRecord(creditRecordId);
        Activity activity = requireActivity(record.getActivityId());
        checkManagePermission(activity, currentUserId, permissionCodes,
                PermissionCodes.CREDIT_REVOKE_OWN,
                PermissionCodes.CREDIT_REVOKE_ALL,
                "无权限操作该活动学分");
        if (GrantStatus.REVOKED.name().equals(record.getGrantStatus())) {
            throw new BusinessException("该学分记录已撤销");
        }
        CreditRecord update = new CreditRecord();
        update.setId(creditRecordId);
        update.setGrantStatus(GrantStatus.REVOKED.name());
        update.setRevokeBy(currentUserId);
        update.setRevokeTime(LocalDateTime.now());
        update.setRevokeReason(reason);
        creditRecordMapper.updateById(update);
    }

    @Override
    public List<CreditRecordVO> listRecords(Long studentId, Long termId, Long currentUserId, List<String> permissionCodes) {
        LambdaQueryWrapper<CreditRecord> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(CreditRecord::getStudentId, studentId);
        }
        if (termId != null) {
            wrapper.eq(CreditRecord::getTermId, termId);
        }

        if (hasPermission(permissionCodes, PermissionCodes.CREDIT_VIEW_ALL)) {
            // no data scope
        } else if (hasPermission(permissionCodes, PermissionCodes.CREDIT_VIEW_OWN)) {
            wrapper.inSql(CreditRecord::getActivityId, "SELECT id FROM activity WHERE organizer_id = " + currentUserId);
        } else if (hasPermission(permissionCodes, PermissionCodes.CREDIT_VIEW_SELF)) {
            wrapper.eq(CreditRecord::getStudentId, currentUserId);
        } else {
            throw new BusinessException(403, "无权限查看学分记录");
        }

        wrapper.orderByDesc(CreditRecord::getGrantTime);
        List<CreditRecord> records = creditRecordMapper.selectList(wrapper);
        List<CreditRecordVO> result = new ArrayList<>();
        for (CreditRecord record : records) {
            result.add(toVO(record));
        }
        return result;
    }

    @Override
    public PersonalCreditSummaryVO personalSummary(Long studentId, Long termId) {
        LambdaQueryWrapper<CreditRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CreditRecord::getStudentId, studentId)
                .eq(CreditRecord::getGrantStatus, GrantStatus.GRANTED.name());
        if (termId != null) {
            wrapper.eq(CreditRecord::getTermId, termId);
        }
        List<CreditRecord> records = creditRecordMapper.selectList(wrapper);
        BigDecimal total = BigDecimal.ZERO;
        for (CreditRecord record : records) {
            total = total.add(record.getCredit());
        }

        PersonalCreditSummaryVO vo = new PersonalCreditSummaryVO();
        vo.setStudentId(studentId);
        SysUser user = sysUserMapper.selectById(studentId);
        vo.setStudentName(user == null ? null : user.getRealName());
        vo.setTermId(termId);
        if (termId != null) {
            BaseTerm term = baseTermMapper.selectById(termId);
            if (term != null) {
                vo.setTermName(term.getTermName());
            }
        }
        vo.setTotalCredit(total);
        return vo;
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
        if (hasPermission(permissionCodes, ownPermission)
                && activity.getOrganizerId().equals(currentUserId)) {
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

    private CreditRecord requireRecord(Long id) {
        CreditRecord record = creditRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(404, "学分记录不存在");
        }
        return record;
    }

    private BaseActivityCategory requireCategory(Long categoryId) {
        BaseActivityCategory category = baseActivityCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(404, "活动类别不存在");
        }
        return category;
    }

    private CreditRecordVO toVO(CreditRecord record) {
        CreditRecordVO vo = new CreditRecordVO();
        vo.setId(record.getId());
        vo.setStudentId(record.getStudentId());
        vo.setActivityId(record.getActivityId());
        vo.setTermId(record.getTermId());
        vo.setCategoryId(record.getCategoryId());
        vo.setCredit(record.getCredit());
        vo.setGrantStatus(record.getGrantStatus());
        vo.setGrantBy(record.getGrantBy());
        vo.setGrantTime(record.getGrantTime());
        vo.setRevokeBy(record.getRevokeBy());
        vo.setRevokeTime(record.getRevokeTime());
        vo.setRevokeReason(record.getRevokeReason());

        SysUser student = sysUserMapper.selectById(record.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getRealName());
            vo.setStudentNo(student.getStudentNo());
        }
        Activity activity = activityMapper.selectById(record.getActivityId());
        if (activity != null) {
            vo.setActivityTitle(activity.getTitle());
        }
        BaseTerm term = baseTermMapper.selectById(record.getTermId());
        if (term != null) {
            vo.setTermName(term.getTermName());
        }
        BaseActivityCategory category = baseActivityCategoryMapper.selectById(record.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getCategoryName());
        }
        if (record.getGrantBy() != null) {
            SysUser grantUser = sysUserMapper.selectById(record.getGrantBy());
            if (grantUser != null) {
                vo.setGrantByName(grantUser.getRealName());
            }
        }
        if (record.getRevokeBy() != null) {
            SysUser revokeUser = sysUserMapper.selectById(record.getRevokeBy());
            if (revokeUser != null) {
                vo.setRevokeByName(revokeUser.getRealName());
            }
        }
        return vo;
    }
}
