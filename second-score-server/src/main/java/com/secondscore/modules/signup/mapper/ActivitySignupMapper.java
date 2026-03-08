package com.secondscore.modules.signup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondscore.modules.signup.entity.ActivitySignup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ActivitySignupMapper extends BaseMapper<ActivitySignup> {

    @Select("""
            SELECT COUNT(1)
            FROM activity_signup
            WHERE activity_id = #{activityId}
              AND signup_status = 'ACTIVE'
            """)
    Long countActiveSignup(Long activityId);
}

