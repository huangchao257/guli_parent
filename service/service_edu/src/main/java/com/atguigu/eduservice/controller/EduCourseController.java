package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.vo.CourseInfoForm;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQuery;
import com.atguigu.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-04-06
 */
@Api(description = "课程管理")
//@CrossOrigin
@RestController
@RequestMapping("/eduservice/course")
public class EduCourseController {

    @Autowired
    private EduCourseService courseService;

    @ApiOperation(value = "查询课程信息")
    @GetMapping("getCoursesInfo")
    public R getCoursesInfo(){
        //TODO 实现带条件带分页的查询
        List<EduCourse> courseList = courseService.list(null);
        return R.ok().data("courseList", courseList);
    }


    @ApiOperation(value = "添加课程相关信息")
    @PostMapping("addCourseInfo")
    public R addCourseInfo(@RequestBody CourseInfoForm courseInfoForm) {
        String courseId = courseService.addCourseInfo(courseInfoForm);
        return R.ok().data("courseId",courseId);
    }

    @ApiOperation(value = "根据课程ID查询课程相关信息")
    @GetMapping("getCourseInfoById/{courseId}")
    public R getCourseInfoById(@PathVariable String courseId) {
        CourseInfoForm courseInfoForm = courseService.getCourseInfoById(courseId);
        return R.ok().data("courseInfo", courseInfoForm);
    }

    @ApiOperation(value = "修改课程相关信息")
    @PostMapping("updateCourseInfo")
    public R updateCourseInfo(@RequestBody CourseInfoForm courseInfoForm) {
        courseService.updateCourseInfo(courseInfoForm);
        return R.ok();
    }

    @ApiOperation(value = "根据课程ID查询课程发布相关信息")
    @GetMapping("getCoursePublishById/{courseId}")
    public R getCoursePublishById(@PathVariable String courseId){
        CoursePublishVo coursePublishVo = courseService.getCoursePublishById(courseId);
        return R.ok().data("coursePublishVo", coursePublishVo);
    }

    @ApiOperation(value = "根据课程id发布课程")
    @PostMapping("publishCourseById/{courseId}")
    public R publishCourseById(@PathVariable String courseId){
        EduCourse course = courseService.getById(courseId);
        course.setStatus("Normal");
        courseService.updateById(course);
        return R.ok();
    }

    @ApiOperation(value = "带条件课程的分页查询")
    @PostMapping("getCoursePageQuery/{page}/{limit}")
    public R getCoursePageQuery(@PathVariable Long page, @PathVariable Long limit,
                                @RequestBody CourseQuery courseQuery){
        String title = courseQuery.getTitle();
        String subjectId = courseQuery.getSubjectId();
        String subjectParentId = courseQuery.getSubjectParentId();
        String teacherId = courseQuery.getTeacherId();
        QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(title)) {
            queryWrapper.like("title",title);
        }
        if (!StringUtils.isEmpty(subjectId)) {
            queryWrapper.eq("subject_id",subjectId);
        }
        if (!StringUtils.isEmpty(subjectParentId)) {
            queryWrapper.eq("subject_parent_id",subjectParentId);
        }
        if (!StringUtils.isEmpty(teacherId)) {
            queryWrapper.eq("teacher_id",teacherId);
        }
        //分页查询
        Page<EduCourse> pageParam = new Page<>(page, limit);
        courseService.page(pageParam, queryWrapper);
        List<EduCourse> records = pageParam.getRecords();
        long total = pageParam.getTotal();
        return R.ok().data("total", total).data("rows", records);
    }

    @ApiOperation(value = "根据课程ID删除课程相关信息")
    @DeleteMapping("delCourseInfoById/{courseId}")
    public R delCourseInfoById(@PathVariable String courseId) {
        courseService.delCourseInfoById(courseId);
        return R.ok();
    }
}

