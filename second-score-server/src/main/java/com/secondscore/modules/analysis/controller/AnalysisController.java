package com.secondscore.modules.analysis.controller;

import com.secondscore.common.api.ApiResponse;
import com.secondscore.modules.analysis.service.AnalysisService;
import com.secondscore.modules.analysis.vo.ActivityRankingVO;
import com.secondscore.modules.analysis.vo.CategoryDistributionVO;
import com.secondscore.modules.analysis.vo.DashboardVO;
import com.secondscore.modules.analysis.vo.TermTrendVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('analysis:view')")
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardVO> dashboard() {
        return ApiResponse.success(analysisService.dashboard());
    }

    @GetMapping("/term-trend")
    public ApiResponse<List<TermTrendVO>> termTrend() {
        return ApiResponse.success(analysisService.termTrend());
    }

    @GetMapping("/category-distribution")
    public ApiResponse<List<CategoryDistributionVO>> categoryDistribution(@RequestParam(required = false) Long termId) {
        return ApiResponse.success(analysisService.categoryDistribution(termId));
    }

    @GetMapping("/activity-ranking")
    public ApiResponse<List<ActivityRankingVO>> activityRanking(@RequestParam(required = false) Long termId,
                                                                @RequestParam(required = false) Integer topN) {
        return ApiResponse.success(analysisService.activityRanking(termId, topN));
    }
}
