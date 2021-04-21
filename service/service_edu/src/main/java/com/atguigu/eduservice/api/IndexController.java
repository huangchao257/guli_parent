package com.atguigu.eduservice.api;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(description = "前台首页展示")
//@CrossOrigin
@RestController
@RequestMapping("/eduservice/index")
public class IndexController {
    @Autowired
    private EduTeacherService teacherService;

    @Autowired
    private EduCourseService courseService;

    @ApiOperation(value = "首页展示8门课程、四位讲师")
    @GetMapping("getCourseTeacher")
    public R getCourseTeacher(){
        //查询八门课程，状态未已发布Normal,取最新的课程
        QueryWrapper<EduCourse> courseWrapper = new QueryWrapper<>();
        courseWrapper.eq("status", "Normal");
        courseWrapper.orderByDesc("gmt_create");
        courseWrapper.last("limit 8");//在sql语句最后添加上该字符串的内容

        List<EduCourse> courseList = courseService.list(courseWrapper);
        //查询四位讲师，取最新的老师
        QueryWrapper<EduTeacher> teacherWrapper = new QueryWrapper<>();
        teacherWrapper.orderByDesc("gmt_create");
        teacherWrapper.last("limit 4");//在sql语句最后添加上该字符串的内容

        List<EduTeacher> teacherList = teacherService.list(teacherWrapper);

        return R.ok().data("courseList", courseList).data("teacherList", teacherList);
    }
}
