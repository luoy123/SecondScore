package com.secondscore.modules.credit.service;

import com.secondscore.modules.credit.vo.CreditGrantResultVO;
import com.secondscore.modules.credit.vo.CreditRecordVO;
import com.secondscore.modules.credit.vo.PersonalCreditSummaryVO;

import java.util.List;

public interface CreditService {
    CreditGrantResultVO grantByActivity(Long activityId, Long currentUserId, List<String> permissionCodes);

    void revoke(Long creditRecordId, String reason, Long currentUserId, List<String> permissionCodes);

    List<CreditRecordVO> listRecords(Long studentId, Long termId, Long currentUserId, List<String> permissionCodes);

    PersonalCreditSummaryVO personalSummary(Long studentId, Long termId);
}
