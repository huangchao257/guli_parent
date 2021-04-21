package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.entity.vo.TeacherQuery;
import com.atguigu.eduservice.service.EduTeacherService;
import com.atguigu.servicebase.handler.GuliException;
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

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2021-03-29
 */
@Api(description = "讲师管理")
@RestController
@RequestMapping("/eduservice/eduteacher")
//@CrossOrigin//支持跨域
public class EduTeacherController {
    @Autowired
    private EduTeacherService teacherService;

    @ApiOperation(value = "所有讲师列表")
    @GetMapping
    public R getAllTeacher() {
        List<EduTeacher> list = teacherService.list(null);
        return R.ok().data("list", list);
    }


    @ApiOperation(value = "根据ID删除讲师")
    @DeleteMapping("{id}")
    public R delTeacher(@PathVariable String id) {
        boolean remove = teacherService.removeById(id);
        if (remove) {
            return R.ok();
        } else {
            return R.error();
        }
    }



    @ApiOperation(value = "分页查询讲师")
    @PostMapping("getTeacherPage/{page}/{limit}")
    public R getTeacherPage(@PathVariable Long page,
                            @PathVariable Long limit,
                            @RequestBody TeacherQuery teacherQuery) {

        QueryWrapper queryWrapper = new QueryWrapper();
        //1.取出查询条件
        String name = teacherQuery.getName();
        Integer level = teacherQuery.getLevel();
        String begin = teacherQuery.getBegin();
        String end = teacherQuery.getEnd();
        //2.判断查询条件是否为空
        if (!StringUtils.isEmpty(name)) {
            queryWrapper.like("name", name);
        }
        if (!StringUtils.isEmpty(level)) {
            queryWrapper.eq("level", level);
        }
        if (!StringUtils.isEmpty(begin)) {
            queryWrapper.ge("mt_create", begin);
        }
        if (!StringUtils.isEmpty(end)) {
            queryWrapper.le("gmt_create", end);
        }

        //3.分页查询
        Page<EduTeacher> pageParm = new Page<>(page, limit);
        teacherService.page(pageParm, queryWrapper);
        List<EduTeacher> records = pageParm.getRecords();
        long total = pageParm.getTotal();
        //方法一
//        Map<String, Object> map = new HashMap<>();
//        map.put("records", records);
//        map.put("total", total);
//        return R.ok().data(map);
        return R.ok().data("records", records).data("total", total);
    }

    @ApiOperation(value = "新增讲师")
    @PostMapping("addTeacher")
    public R addTeacher(@RequestBody EduTeacher teacher) {
        teacherService.save(teacher);
        return R.ok();
    }

    @ApiOperation(value = "根据ID查询讲师")
    @GetMapping("getTeacherById/{id}")
    public R getTeacherById(@PathVariable("id") String id) {
        EduTeacher teacher = teacherService.getById(id);
        return R.ok().data("teacher", teacher);
    }

    @ApiOperation(value = "根据ID修改讲师")
    @PutMapping("updateTeacher")
    public R updateTeacher(@RequestBody EduTeacher teacher) {
        boolean flag = teacherService.updateById(teacher);
        if (flag) {
            return R.ok();
        }else {
            return R.error();
        }
    }

}

