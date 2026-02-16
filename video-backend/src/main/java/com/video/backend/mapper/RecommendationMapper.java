package com.video.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.video.backend.entity.Recommendation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecommendationMapper extends BaseMapper<Recommendation> {}

