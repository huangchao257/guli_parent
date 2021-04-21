package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.client.VodClient;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.entity.vo.VideoInfoForm;
import com.atguigu.eduservice.mapper.EduVideoMapper;
import com.atguigu.eduservice.service.EduVideoService;
import com.atguigu.servicebase.handler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-04-08
 */
@Service
public class EduVideoServiceImpl extends ServiceImpl<EduVideoMapper, EduVideo> implements EduVideoService {

    @Autowired
    private VodClient vodClient;

    @Override
    public void saveVideoInfo(VideoInfoForm videoInfo) {
        EduVideo video = new EduVideo();
        BeanUtils.copyProperties(videoInfo, video);
        int i = baseMapper.insert(video);
        if (i != 1){
            throw new GuliException(20001, "课时信息保存失败");
        }
    }

    @Override
    public VideoInfoForm getVideInfoById(String id) {
        EduVideo eduVideo = baseMapper.selectById(id);
        VideoInfoForm videoInfo = new VideoInfoForm();
        BeanUtils.copyProperties(eduVideo, videoInfo);
        return videoInfo;
    }

    @Override
    public void updateVideInfoById(VideoInfoForm videoInfo) {
        //通过课时id获取课时对象——乐观锁
        EduVideo video = baseMapper.selectById(videoInfo.getId());
        BeanUtils.copyProperties(videoInfo,video);
        int i = baseMapper.updateById(video);
        if (i != 1){
            throw new GuliException(20001, "课时信息保存失败");
        }
    }

    @Override
    public void removeVideoById(String id) {
        //根据id查询课时信息
        EduVideo eduVideo = baseMapper.selectById(id);
        if (eduVideo != null) {
            //删除视频
            vodClient.removeVideo(eduVideo.getVideoSourceId());
        }
        baseMapper.deleteById(id);
    }

    @Override
    public void delVideoByCourseId(String courseId) {
        //根据课程id获取所有视频列表
        QueryWrapper<EduVideo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.select("video_source_id");
        List<EduVideo> eduVideos = baseMapper.selectList(queryWrapper);
        //获取所有云端原始视频id
        List<String> videoSourceIdList = new ArrayList<>();
        for (EduVideo eduVideo : eduVideos) {
            String videoSourceId = eduVideo.getVideoSourceId();
            if (!StringUtils.isEmpty(videoSourceId)){
                videoSourceIdList.add(videoSourceId);
            }
        }
        //批量删除视频
        if (videoSourceIdList.size()>0) {
            vodClient.removeVideoList(videoSourceIdList);
        }
    }
}
