package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduChapter;
import com.atguigu.eduservice.entity.vo.ChapterVo;
import com.atguigu.eduservice.service.EduChapterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-04-08
 */
@Api(description = "章节管理")
//@CrossOrigin
@RestController
@RequestMapping("/eduservice/chapter")
public class EduChapterController {

    @Autowired
    private EduChapterService chapterService;

    @ApiOperation(value = "根据课程id获取嵌套章节列表")
    @GetMapping("getChapterVideo/{courseId}")
    public R getChapterVideoById(@PathVariable String courseId) {
        List<ChapterVo> chapterVideoList = chapterService.getChapterListById(courseId);
        return R.ok().data("chapterVideoList", chapterVideoList);
    }

    @ApiOperation(value = "根据ID查询章节")
    @GetMapping("getChapterById/{id}")
    public R getChapterById(@PathVariable String id) {
        EduChapter chapter = chapterService.getById(id);
        return R.ok().data("chapter", chapter);
    }

    @ApiOperation(value = "新增章节")
    @PostMapping("addChapter")
    public R addChapter(@RequestBody EduChapter chapter) {
        chapterService.save(chapter);
        return R.ok();
    }

    @ApiOperation(value = "根据ID修改章节")
    @PutMapping("updateChapter")
    public R updateChapter(@RequestBody EduChapter chapter) {
        chapterService.updateById(chapter);
        return R.ok();
    }

    @ApiOperation(value = "根据ID删除章节")
    @DeleteMapping("delChapter/{id}")
    public R removeById(@PathVariable String id) {
        boolean flag = chapterService.removeById(id);
        if (flag) {
            return R.ok();
        } else {
            return R.error().message("删除失败");
        }
    }

}

