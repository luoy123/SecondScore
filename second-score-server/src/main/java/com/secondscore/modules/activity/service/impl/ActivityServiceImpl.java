package com.secondscore.modules.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.secondscore.common.constants.PermissionCodes;
import com.secondscore.common.enums.ActivityStatus;
import com.secondscore.common.exception.BusinessException;
import com.secondscore.modules.activity.dto.ActivityCreateRequest;
import com.secondscore.modules.activity.dto.ActivityUpdateRequest;
import com.secondscore.modules.activity.entity.Activity;
import com.secondscore.modules.activity.mapper.ActivityMapper;
import com.secondscore.modules.activity.service.ActivityService;
import com.secondscore.modules.activity.vo.ActivityVO;
import com.secondscore.modules.base.entity.BaseActivityCategory;
import com.secondscore.modules.base.entity.BaseTerm;
import com.secondscore.modules.base.mapper.BaseActivityCategoryMapper;
import com.secondscore.modules.base.mapper.BaseTermMapper;
import com.secondscore.modules.signup.entity.ActivitySignup;
import com.secondscore.modules.signup.mapper.ActivitySignupMapper;
import com.secondscore.modules.user.entity.SysUser;
import com.secondscore.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;
    private final BaseActivityCategoryMapper baseActivityCategoryMapper;
    private final BaseTermMapper baseTermMapper;
    private final SysUserMapper sysUserMapper;
    private final ActivitySignupMapper activitySignupMapper;

    @Override
    public Long create(ActivityCreateRequest request, Long currentUserId, List<String> permissionCodes) {
        validateTimeRange(request.getSignupStart(), request.getSignupEnd(), request.getStartTime(), request.getEndTime());
        requireCategory(request.getCategoryId());
        requireTerm(request.getTermId());

        Long organizerId = resolveOrganizerId(request.getOrganizerId(), currentUserId, permissionCodes);
        requireUser(organizerId);

        Activity activity = new Activity();
        fillActivityForSave(activity, request, organizerId);
        activity.setStatus(ActivityStatus.DRAFT.name());
        activityMapper.insert(activity);
        return activity.getId();
    }

    @Override
    public void update(Long activityId, ActivityUpdateRequest request, Long currentUserId, List<String> permissionCodes) {
        Activity existed = requireActivity(activityId);
        checkManagePermission(existed, currentUserId, permissionCodes);
        if (!ActivityStatus.DRAFT.name().equals(existed.getStatus())) {
            throw new BusinessException("仅草稿状态活动可编辑");
        }

        validateTimeRange(request.getSignupStart(), request.getSignupEnd(), request.getStartTime(), request.getEndTime());
        requireCategory(request.getCategoryId());
        requireTerm(request.getTermId());

        Long organizerId = resolveOrganizerId(request.getOrganizerId(), existed.getOrganizerId(), permissionCodes);
        requireUser(organizerId);

        Activity update = new Activity();
        update.setId(activityId);
        fillActivityForSave(update, request, organizerId);
        update.setStatus(existed.getStatus());
        activityMapper.updateById(update);
    }

    @Override
    public void publish(Long activityId, Long currentUserId, List<String> permissionCodes) {
        Activity activity = requireActivity(activityId);
        checkManagePermission(activity, currentUserId, permissionCodes);
        if (!ActivityStatus.DRAFT.name().equals(activity.getStatus())) {
            throw new BusinessException("只有草稿活动可以发布");
        }
        Activity update = new Activity();
        update.setId(activityId);
        update.setStatus(ActivityStatus.PUBLISHED.name());
        activityMapper.updateById(update);
    }

    @Override
    public void finish(Long activityId, Long currentUserId, List<String> permissionCodes) {
        Activity activity = requireActivity(activityId);
        checkManagePermission(activity, currentUserId, permissionCodes);
        if (!ActivityStatus.PUBLISHED.name().equals(activity.getStatus())) {
            throw new BusinessException("只有已发布活动可以完结");
        }
        Activity update = new Activity();
        update.setId(activityId);
        update.setStatus(ActivityStatus.FINISHED.name());
        activityMapper.updateById(update);
    }

    @Override
    public List<ActivityVO> list(String title, Long termId, String status, Long currentUserId, List<String> permissionCodes) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isBlank()) {
            wrapper.like(Activity::getTitle, title);
        }
        if (termId != null) {
            wrapper.eq(Activity::getTermId, termId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(Activity::getStatus, status);
        }

        if (hasPermission(permissionCodes, PermissionCodes.ACTIVITY_MANAGE_ALL)) {
            // no extra data scope
        } else if (hasPermission(permissionCodes, PermissionCodes.ACTIVITY_MANAGE_OWN)) {
            wrapper.eq(Activity::getOrganizerId, currentUserId);
        } else {
            wrapper.in(Activity::getStatus, ActivityStatus.PUBLISHED.name(), ActivityStatus.FINISHED.name());
        }

        wrapper.orderByDesc(Activity::getStartTime);
        List<Activity> activities = activityMapper.selectList(wrapper);
        List<ActivityVO> result = new ArrayList<>();
        for (Activity activity : activities) {
            result.add(toVO(activity));
        }
        return result;
    }

    @Override
    public ActivityVO detail(Long activityId, Long currentUserId, List<String> permissionCodes) {
        Activity activity = requireActivity(activityId);
        if (hasPermission(permissionCodes, PermissionCodes.ACTIVITY_MANAGE_ALL)) {
            return toVO(activity);
        }
        if (hasPermission(permissionCodes, PermissionCodes.ACTIVITY_MANAGE_OWN)
                && activity.getOrganizerId().equals(currentUserId)) {
            return toVO(activity);
        }
        if (ActivityStatus.PUBLISHED.name().equals(activity.getStatus())
                || ActivityStatus.FINISHED.name().equals(activity.getStatus())) {
            return toVO(activity);
        }
        throw new BusinessException(403, "无权查看该活动");
    }

    private void fillActivityForSave(Activity target, ActivityCreateRequest request, Long organizerId) {
        target.setTitle(request.getTitle());
        target.setCategoryId(request.getCategoryId());
        target.setTermId(request.getTermId());
        target.setOrganizerId(organizerId);
        target.setLocation(request.getLocation());
        target.setStartTime(request.getStartTime());
        target.setEndTime(request.getEndTime());
        target.setSignupStart(request.getSignupStart());
        target.setSignupEnd(request.getSignupEnd());
        target.setCapacity(request.getCapacity());
        target.setCredit(request.getCredit());
        target.setDescription(request.getDescription());
    }

    private void fillActivityForSave(Activity target, ActivityUpdateRequest request, Long organizerId) {
        target.setTitle(request.getTitle());
        target.setCategoryId(request.getCategoryId());
        target.setTermId(request.getTermId());
        target.setOrganizerId(organizerId);
        target.setLocation(request.getLocation());
        target.setStartTime(request.getStartTime());
        target.setEndTime(request.getEndTime());
        target.setSignupStart(request.getSignupStart());
        target.setSignupEnd(request.getSignupEnd());
        target.setCapacity(request.getCapacity());
        target.setCredit(request.getCredit());
        target.setDescription(request.getDescription());
    }

    private void validateTimeRange(LocalDateTime signupStart, LocalDateTime signupEnd,
                                   LocalDateTime startTime, LocalDateTime endTime) {
        if (!signupStart.isBefore(signupEnd)) {
            throw new BusinessException("报名开始时间必须早于报名结束时间");
        }
        if (!startTime.isBefore(endTime)) {
            throw new BusinessException("活动开始时间必须早于结束时间");
        }
        if (signupEnd.isAfter(startTime)) {
            throw new BusinessException("报名结束时间不能晚于活动开始时间");
        }
    }

    private Activity requireActivity(Long activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }
        return activity;
    }

    private BaseActivityCategory requireCategory(Long categoryId) {
        BaseActivityCategory category = baseActivityCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(404, "活动类别不存在");
        }
        return category;
    }

    private BaseTerm requireTerm(Long termId) {
        BaseTerm term = baseTermMapper.selectById(termId);
        if (term == null) {
            throw new BusinessException(404, "学期不存在");
        }
        return term;
    }

    private SysUser requireUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private Long resolveOrganizerId(Long organizerId, Long fallbackUserId, List<String> permissionCodes) {
        if (hasPermission(permissionCodes, PermissionCodes.ACTIVITY_MANAGE_ALL)) {
            return organizerId != null ? organizerId : fallbackUserId;
        }
        return fallbackUserId;
    }

    private void checkManagePermission(Activity activity, Long currentUserId, List<String> permissionCodes) {
        if (hasPermission(permissionCodes, PermissionCodes.ACTIVITY_MANAGE_ALL)) {
            return;
        }
        if (hasPermission(permissionCodes, PermissionCodes.ACTIVITY_MANAGE_OWN)
                && activity.getOrganizerId().equals(currentUserId)) {
            return;
        }
        throw new BusinessException(403, "无权限操作该活动");
    }

    private boolean hasPermission(List<String> permissions, String code) {
        return permissions != null && permissions.contains(code);
    }

    private ActivityVO toVO(Activity activity) {
        ActivityVO vo = new ActivityVO();
        vo.setId(activity.getId());
        vo.setTitle(activity.getTitle());
        vo.setCategoryId(activity.getCategoryId());
        vo.setTermId(activity.getTermId());
        vo.setOrganizerId(activity.getOrganizerId());
        vo.setLocation(activity.getLocation());
        vo.setStartTime(activity.getStartTime());
        vo.setEndTime(activity.getEndTime());
        vo.setSignupStart(activity.getSignupStart());
        vo.setSignupEnd(activity.getSignupEnd());
        vo.setCapacity(activity.getCapacity());
        vo.setCredit(activity.getCredit());
        vo.setStatus(activity.getStatus());
        vo.setDescription(activity.getDescription());

        BaseActivityCategory category = baseActivityCategoryMapper.selectById(activity.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getCategoryName());
        }
        BaseTerm term = baseTermMapper.selectById(activity.getTermId());
        if (term != null) {
            vo.setTermName(term.getTermName());
        }
        SysUser organizer = sysUserMapper.selectById(activity.getOrganizerId());
        if (organizer != null) {
            vo.setOrganizerName(organizer.getRealName());
        }
        Long signupCount = activitySignupMapper.selectCount(new LambdaQueryWrapper<ActivitySignup>()
                .eq(ActivitySignup::getActivityId, activity.getId())
                .eq(ActivitySignup::getSignupStatus, "ACTIVE"));
        vo.setSignupCount(signupCount == null ? 0 : signupCount.intValue());
        return vo;
    }
}
