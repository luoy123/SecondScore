package com.secondscore.modules.credit.controller;

import com.secondscore.common.api.ApiResponse;
import com.secondscore.common.constants.PermissionCodes;
import com.secondscore.common.security.SecurityUtils;
import com.secondscore.modules.credit.dto.CreditRevokeRequest;
import com.secondscore.modules.credit.service.CreditService;
import com.secondscore.modules.credit.vo.CreditGrantResultVO;
import com.secondscore.modules.credit.vo.CreditRecordVO;
import com.secondscore.modules.credit.vo.PersonalCreditSummaryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @PostMapping("/activities/{activityId}/credits/grant")
    @PreAuthorize("hasAnyAuthority('credit:grant:own','credit:grant:all')")
    public ApiResponse<CreditGrantResultVO> grant(@PathVariable Long activityId) {
        return ApiResponse.success(creditService.grantByActivity(
                activityId,
                SecurityUtils.getCurrentUserId(),
                SecurityUtils.getCurrentPermissionCodes()));
    }

    @PostMapping("/credit-records/{id}/revoke")
    @PreAuthorize("hasAnyAuthority('credit:revoke:own','credit:revoke:all')")
    public ApiResponse<Void> revoke(@PathVariable Long id, @Valid @RequestBody CreditRevokeRequest request) {
        creditService.revoke(id, request.getReason(), SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentPermissionCodes());
        return ApiResponse.success();
    }

    @GetMapping("/credit-records")
    @PreAuthorize("hasAnyAuthority('credit:view:self','credit:view:own','credit:view:all')")
    public ApiResponse<List<CreditRecordVO>> listRecords(@RequestParam(required = false) Long studentId,
                                                         @RequestParam(required = false) Long termId) {
        return ApiResponse.success(creditService.listRecords(
                studentId,
                termId,
                SecurityUtils.getCurrentUserId(),
                SecurityUtils.getCurrentPermissionCodes()));
    }

    @GetMapping("/credit-summary/personal")
    @PreAuthorize("hasAnyAuthority('credit:view:self','credit:view:own','credit:view:all')")
    public ApiResponse<PersonalCreditSummaryVO> personalSummary(@RequestParam(required = false) Long studentId,
                                                                @RequestParam(required = false) Long termId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Long targetStudentId;
        if (SecurityUtils.hasPermission(PermissionCodes.CREDIT_VIEW_ALL)) {
            targetStudentId = studentId == null ? currentUserId : studentId;
        } else {
            targetStudentId = currentUserId;
        }
        return ApiResponse.success(creditService.personalSummary(targetStudentId, termId));
    }
}
