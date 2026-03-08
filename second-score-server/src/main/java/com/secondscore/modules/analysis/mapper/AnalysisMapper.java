package com.secondscore.modules.analysis.mapper;

import com.secondscore.modules.analysis.vo.ActivityRankingVO;
import com.secondscore.modules.analysis.vo.CategoryDistributionVO;
import com.secondscore.modules.analysis.vo.DashboardVO;
import com.secondscore.modules.analysis.vo.TermTrendVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AnalysisMapper {

    @Select("""
            SELECT
              (SELECT COUNT(*) FROM activity) AS totalActivities,
              (SELECT COUNT(*) FROM activity_signup WHERE signup_status = 'ACTIVE') AS totalSignups,
              (SELECT COALESCE(SUM(credit), 0) FROM credit_record WHERE grant_status = 'GRANTED') AS totalGrantedCredit,
              (SELECT COUNT(DISTINCT student_id) FROM credit_record WHERE grant_status = 'GRANTED') AS totalCreditedStudents
            """)
    DashboardVO dashboard();

    @Select("""
            SELECT
              t.id AS termId,
              t.term_name AS termName,
              COALESCE(SUM(cr.credit), 0) AS totalCredit
            FROM base_term t
            LEFT JOIN credit_record cr
              ON cr.term_id = t.id
             AND cr.grant_status = 'GRANTED'
            GROUP BY t.id, t.term_name
            ORDER BY t.id
            """)
    List<TermTrendVO> termTrend();

    @Select("""
            <script>
            SELECT
              c.id AS categoryId,
              c.category_name AS categoryName,
              COALESCE(SUM(cr.credit), 0) AS totalCredit
            FROM base_activity_category c
            LEFT JOIN credit_record cr
              ON cr.category_id = c.id
             AND cr.grant_status = 'GRANTED'
            <if test='termId != null'>
             AND cr.term_id = #{termId}
            </if>
            GROUP BY c.id, c.category_name
            ORDER BY totalCredit DESC, c.id
            </script>
            """)
    List<CategoryDistributionVO> categoryDistribution(@Param("termId") Long termId);

    @Select("""
            <script>
            SELECT
              a.id AS activityId,
              a.title AS activityTitle,
              COALESCE(p.participant_count, 0) AS participantCount,
              COALESCE(c.total_credit, 0) AS totalCredit
            FROM activity a
            LEFT JOIN (
              SELECT activity_id, COUNT(*) AS participant_count
              FROM activity_participation
              WHERE attendance_status = 'PRESENT'
              GROUP BY activity_id
            ) p ON p.activity_id = a.id
            LEFT JOIN (
              SELECT activity_id, SUM(credit) AS total_credit
              FROM credit_record
              WHERE grant_status = 'GRANTED'
              <if test='termId != null'>
                AND term_id = #{termId}
              </if>
              GROUP BY activity_id
            ) c ON c.activity_id = a.id
            <if test='termId != null'>
             WHERE a.term_id = #{termId}
            </if>
            ORDER BY participantCount DESC, totalCredit DESC, a.id
            LIMIT #{topN}
            </script>
            """)
    List<ActivityRankingVO> activityRanking(@Param("termId") Long termId, @Param("topN") Integer topN);
}

