package com.video.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.video.backend.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    void incrementViewCount(@Param("videoId") Long videoId);
    void incrementLikeCount(@Param("videoId") Long videoId);
    void incrementShareCount(@Param("videoId") Long videoId);
    void incrementCommentCount(@Param("videoId") Long videoId);
    void incrementCollectCount(@Param("videoId") Long videoId);
}
