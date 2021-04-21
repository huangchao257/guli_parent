package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.client.OssClient;
import com.atguigu.eduservice.client.VodClient;
import com.atguigu.eduservice.entity.EduChapter;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduCourseDescription;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.entity.vo.CourseInfoForm;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQueryVo;
import com.atguigu.eduservice.entity.vo.CourseWebVo;
import com.atguigu.eduservice.mapper.EduCourseMapper;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduCourseDescriptionService;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduVideoService;
import com.atguigu.servicebase.handler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-04-06
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    private EduCourseDescriptionService courseDescriptionService;

    @Autowired
    private EduVideoService videoService;

    @Autowired
    private EduChapterService chapterService;

    @Autowired
    private OssClient ossClient;

    @Override
    public String addCourseInfo(CourseInfoForm courseInfoForm) {
        //1.保存课程基本信息
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoForm, eduCourse);
        int insert = baseMapper.insert(eduCourse);
        //2.判断是否保存成功
        if (insert != 1){
            throw new GuliException(20001, "保存课程相关信息失败");
        }
        //3.获取课程id
        String id = eduCourse.getId();
        //4.保存描述信息
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        eduCourseDescription.setId(id);
        eduCourseDescription.setDescription(courseInfoForm.getDescription());

        courseDescriptionService.save(eduCourseDescription);
        return id;
    }

    @Override
    public CourseInfoForm getCourseInfoById(String courseId) {
        //1.根据课程ID查询课程相关信息
        EduCourse eduCourse = baseMapper.selectById(courseId);
        EduCourseDescription eduCourseDescription = courseDescriptionService.getById(courseId);
        //2.封装到courseInfoForm对象中
        CourseInfoForm courseInfoForm = new CourseInfoForm();
        BeanUtils.copyProperties(eduCourse, courseInfoForm);
        courseInfoForm.setDescription(eduCourseDescription.getDescription());
        return courseInfoForm;
    }

    @Override
    public void updateCourseInfo(CourseInfoForm courseInfoForm) {
        //1.修改课程基本信息
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoForm, eduCourse);
        int update = baseMapper.updateById(eduCourse);
        //2.判断是否保存成功
        if (update != 1){
            throw new GuliException(20001, "修改课程相关信息失败");
        }

        //3.修改描述信息
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        eduCourseDescription.setId(courseInfoForm.getId());
        eduCourseDescription.setDescription(courseInfoForm.getDescription());

        courseDescriptionService.updateById(eduCourseDescription);
    }

    @Override
    public CoursePublishVo getCoursePublishById(String id) {
        CoursePublishVo coursePublishVo = baseMapper.getCoursePublishById(id);
        return coursePublishVo;
    }

    @Override
    public void delCourseInfoById(String courseId) {
        //1、删除视频信息
        //根据课程id删除所有视频
        videoService.delVideoByCourseId(courseId);

        //2、删除相关小节信息
        QueryWrapper<EduVideo> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("course_id", courseId);
        videoService.remove(videoQueryWrapper);
        //3、删除相关章节信息
        QueryWrapper<EduChapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq("course_id", courseId);
        chapterService.remove(chapterQueryWrapper);

        //4、删除课程描述信息
        courseDescriptionService.removeById(courseId);

        //5、删除课程封面
        EduCourse eduCourse = baseMapper.selectById(courseId);
        ossClient.delFile(eduCourse.getCover());

        //6、删除课程信息
        int i = baseMapper.deleteById(courseId);
        if (i != 1){
            throw new GuliException(20001, "删除课程信息失败");
        }
    }

    @Override
    public Map<String, Object> getCourseApiPageVo(Page<EduCourse> pageParam, CourseQueryVo courseQueryVo) {
        //从对象中取出参数，并设置查询条件
        QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();
        //前台查询课程必须条件 课程必须是已发布的
        queryWrapper.eq("status", "Normal");

        if (!StringUtils.isEmpty(courseQueryVo.getSubjectParentId())) {
            queryWrapper.eq("subject_parent_id", courseQueryVo.getSubjectParentId());
        }

        if (!StringUtils.isEmpty(courseQueryVo.getSubjectId())) {
            queryWrapper.eq("subject_id", courseQueryVo.getSubjectId());
        }

        if (!StringUtils.isEmpty(courseQueryVo.getBuyCountSort())) {
            queryWrapper.orderByDesc("buy_count");
        }

        if (!StringUtils.isEmpty(courseQueryVo.getGmtCreateSort())) {
            queryWrapper.orderByDesc("gmt_create");
        }

        if (!StringUtils.isEmpty(courseQueryVo.getPriceSort())) {
            queryWrapper.orderByDesc("price");
        }

        //分页查询
        baseMapper.selectPage(pageParam, queryWrapper);

        //封装数据
        List<EduCourse> records = pageParam.getRecords();
        long current = pageParam.getCurrent();
        long pages = pageParam.getPages();
        long size = pageParam.getSize();
        long total = pageParam.getTotal();
        boolean hasNext = pageParam.hasNext();
        boolean hasPrevious = pageParam.hasPrevious();

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);
        return map;
    }

    @Override
    public CourseWebVo getCourseWebVo(String id) {
        CourseWebVo courseWebVo = baseMapper.getCourseWebVo(id);
        return courseWebVo;
    }
}
