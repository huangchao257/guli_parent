package com.atguigu.eduservice.service;

import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.entity.vo.VideoInfoForm;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author hc
 * @since 2021-04-08
 */
public interface EduVideoService extends IService<EduVideo> {

    void saveVideoInfo(VideoInfoForm videoInfo);

    VideoInfoForm getVideInfoById(String id);

    void updateVideInfoById(VideoInfoForm videoInfo);

    void removeVideoById(String id);

    void delVideoByCourseId(String courseId);
}
