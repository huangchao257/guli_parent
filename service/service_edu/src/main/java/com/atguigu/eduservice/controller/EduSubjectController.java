package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.vo.OneSubjectVo;
import com.atguigu.eduservice.service.EduSubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-04-02
 */
@Api(description = "课程分类管理")
@RestController
//@CrossOrigin
@RequestMapping("/eduservice/subject")
public class EduSubjectController {

    @Autowired
    private EduSubjectService subjectService;

    //添加课程分类
    @ApiOperation(value = "Excel批量导入")
    @PostMapping("addSubject")
    public R addSubject(MultipartFile file){
        subjectService.addSubject(file,subjectService);//手动注入subjectService
        return R.ok();
    }

    //获取所有课程分类信息
    @ApiOperation(value = "获取所有课程分类信息")
    @GetMapping("getAllSubject")
    private R getAllSubject() {
        List<OneSubjectVo> allSubject = subjectService.getAllSubject();
        return R.ok().data("allSubject", allSubject);
    }


}

