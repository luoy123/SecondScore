package com.secondscore.modules.analysis.service;

import com.secondscore.modules.analysis.vo.ActivityRankingVO;
import com.secondscore.modules.analysis.vo.CategoryDistributionVO;
import com.secondscore.modules.analysis.vo.DashboardVO;
import com.secondscore.modules.analysis.vo.TermTrendVO;

import java.util.List;

public interface AnalysisService {
    DashboardVO dashboard();

    List<TermTrendVO> termTrend();

    List<CategoryDistributionVO> categoryDistribution(Long termId);

    List<ActivityRankingVO> activityRanking(Long termId, Integer topN);
}

