package com.secondscore.modules.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondscore.modules.activity.entity.Activity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
}

