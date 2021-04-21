package com.atguigu.eduservice.service;

import com.atguigu.eduservice.entity.EduChapter;
import com.atguigu.eduservice.entity.vo.ChapterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author hc
 * @since 2021-04-08
 */
public interface EduChapterService extends IService<EduChapter> {

    List<ChapterVo> getChapterListById(String courseId);
}
