package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.vo.VideoInfoForm;
import com.atguigu.eduservice.service.EduVideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-04-08
 */
@Api(description = "课时管理")
//@CrossOrigin //跨域
@RestController
@RequestMapping("/eduservice/video")
public class EduVideoController {

    @Autowired
    private EduVideoService videoService;

    @ApiOperation(value = "新增课时")
    @PostMapping("addVideoInfo")
    private R save(@RequestBody VideoInfoForm videoInfo) {
        videoService.saveVideoInfo(videoInfo);
        return R.ok();
    }

    @ApiOperation(value = "根据ID查询课时")
    @GetMapping("getVideoInfoById/{id}")
    public R getVideoInfoById(@PathVariable String id) {
        VideoInfoForm videoInfo = videoService.getVideInfoById(id);
        return R.ok().data("videoInfo", videoInfo);
    }

    @ApiOperation(value = "更新课时")
    @PutMapping("updateVideoInfo")
    public R updateVideoInfoById(@RequestBody VideoInfoForm videoInfo) {
        videoService.updateVideInfoById(videoInfo);
        return R.ok();
    }

    @ApiOperation(value = "根据ID删除课时")
    @DeleteMapping("delVideo/{id}")
    public R removeById(@PathVariable String id){
        videoService.removeVideoById(id);
        return R.ok();
    }
}

