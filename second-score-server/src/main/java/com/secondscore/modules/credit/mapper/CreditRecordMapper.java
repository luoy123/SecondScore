package com.secondscore.modules.credit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondscore.modules.credit.entity.CreditRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface CreditRecordMapper extends BaseMapper<CreditRecord> {

    @Select("""
            SELECT COALESCE(SUM(credit), 0)
            FROM credit_record
            WHERE student_id = #{studentId}
              AND term_id = #{termId}
              AND category_id = #{categoryId}
              AND grant_status = 'GRANTED'
            """)
    BigDecimal sumGrantedCreditByTermAndCategory(Long studentId, Long termId, Long categoryId);
}

