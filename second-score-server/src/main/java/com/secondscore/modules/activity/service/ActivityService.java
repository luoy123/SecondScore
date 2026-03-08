package com.secondscore.modules.activity.service;

import com.secondscore.modules.activity.dto.ActivityCreateRequest;
import com.secondscore.modules.activity.dto.ActivityUpdateRequest;
import com.secondscore.modules.activity.vo.ActivityVO;

import java.util.List;

public interface ActivityService {
    Long create(ActivityCreateRequest request, Long currentUserId, List<String> permissionCodes);

    void update(Long activityId, ActivityUpdateRequest request, Long currentUserId, List<String> permissionCodes);

    void publish(Long activityId, Long currentUserId, List<String> permissionCodes);

    void finish(Long activityId, Long currentUserId, List<String> permissionCodes);

    List<ActivityVO> list(String title, Long termId, String status, Long currentUserId, List<String> permissionCodes);

    ActivityVO detail(Long activityId, Long currentUserId, List<String> permissionCodes);
}
