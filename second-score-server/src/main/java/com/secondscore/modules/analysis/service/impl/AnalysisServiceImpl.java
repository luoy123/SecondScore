package com.secondscore.modules.analysis.service.impl;

import com.secondscore.modules.analysis.mapper.AnalysisMapper;
import com.secondscore.modules.analysis.service.AnalysisService;
import com.secondscore.modules.analysis.vo.ActivityRankingVO;
import com.secondscore.modules.analysis.vo.CategoryDistributionVO;
import com.secondscore.modules.analysis.vo.DashboardVO;
import com.secondscore.modules.analysis.vo.TermTrendVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final AnalysisMapper analysisMapper;

    @Override
    public DashboardVO dashboard() {
        return analysisMapper.dashboard();
    }

    @Override
    public List<TermTrendVO> termTrend() {
        return analysisMapper.termTrend();
    }

    @Override
    public List<CategoryDistributionVO> categoryDistribution(Long termId) {
        return analysisMapper.categoryDistribution(termId);
    }

    @Override
    public List<ActivityRankingVO> activityRanking(Long termId, Integer topN) {
        int finalTopN = (topN == null || topN <= 0) ? 10 : Math.min(topN, 50);
        return analysisMapper.activityRanking(termId, finalTopN);
    }
}

