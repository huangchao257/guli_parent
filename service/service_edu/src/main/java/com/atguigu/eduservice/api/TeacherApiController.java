package com.atguigu.eduservice.api;

import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.entity.vo.TeacherQuery;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "前台名师展示")
//@CrossOrigin
@RestController
@RequestMapping("/eduservice/teacherapi")
public class TeacherApiController {

    @Autowired
    private EduTeacherService teacherService;

    @Autowired
    private EduCourseService courseService;

    @ApiOperation(value = "前台分页查询讲师")
    @GetMapping("getTeacherApiPage/{page}/{limit}")
    public R getTeacherPage(@PathVariable Long page,
                            @PathVariable Long limit) {

        //分页查询
        Page<EduTeacher> pageParam = new Page<>(page, limit);
        Map<String, Object> teacherMap = teacherService.getTeacherApiPage(pageParam);
        return R.ok().data(teacherMap);
    }

    @ApiOperation(value = "前端讲师详情、相关课程")
    @GetMapping("getTeacherApiCourse/{id}")
    public R getTeacherApiCourse(@PathVariable String id) {
        //1.根据讲师id查询讲师信息
        EduTeacher teacher = teacherService.getById(id);
        //2.根据讲师id查询已发布的相关课程
        QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "Normal");
        queryWrapper.eq("teacher_id", id);
        queryWrapper.orderByDesc("gmt_create");
        List<EduCourse> courseList = courseService.list(queryWrapper);
        return R.ok().data("teacher", teacher).data("courseList", courseList);
    }

}
