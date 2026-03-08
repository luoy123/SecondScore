package com.secondscore.modules.signup.service;

import com.secondscore.modules.signup.dto.ParticipationMarkRequest;
import com.secondscore.modules.signup.vo.ParticipationVO;
import com.secondscore.modules.signup.vo.SignupVO;

import java.util.List;

public interface SignupService {
    void signup(Long activityId, Long studentId);

    void cancelSignup(Long activityId, Long studentId);

    List<SignupVO> listMySignups(Long studentId);

    List<SignupVO> listSignups(Long activityId, Long currentUserId, List<String> permissionCodes);

    void approveSignup(Long signupId, Long reviewerId, List<String> permissionCodes);

    void rejectSignup(Long signupId, String reason, Long reviewerId, List<String> permissionCodes);

    void markParticipation(Long activityId, ParticipationMarkRequest request, Long currentUserId, List<String> permissionCodes);

    List<ParticipationVO> listParticipation(Long activityId, Long currentUserId, List<String> permissionCodes);
}
