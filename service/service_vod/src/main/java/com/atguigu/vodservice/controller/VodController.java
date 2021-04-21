package com.atguigu.vodservice.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.atguigu.commonutils.R;
import com.atguigu.servicebase.handler.GuliException;
import com.atguigu.vodservice.service.VideoService;
import com.atguigu.vodservice.utils.AliyunVodSDKUtils;
import com.atguigu.vodservice.utils.ConstantPropertiesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(description = "视频管理")
//@CrossOrigin
@RestController
@RequestMapping("/eduvod/video")
public class VodController {

    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "上传视频")
    @PostMapping("uploadVideo")
    public R uploadVideo(MultipartFile file) {
        String videoId = videoService.uploadVideo(file);
        return R.ok().message("视频上传成功").data("videoId", videoId);
    }

    @ApiOperation(value = "删除指定视频")
    @DeleteMapping("delVideo/{videoId}")
    public R removeVideo(@PathVariable String videoId){

        videoService.removeVideo(videoId);
        return R.ok().message("视频删除成功");
    }

    @ApiOperation(value = "批量删除视频")
    @DeleteMapping("delVideoBatch")
    public R removeVideoList(@RequestParam("videoIdList") List<String> videoIdList){
        videoService.removeVideoList(videoIdList);
        return R.ok().message("视频删除成功");
    }

    @ApiOperation(value = "获取视频播放凭证")
    @GetMapping("getVideoAuth/{videoId}")
    public R getVideoAuth(@PathVariable String videoId) {

        DefaultAcsClient client = null;
        String playAuth = null;

        try {
            //1.创建初始化对象
            client = AliyunVodSDKUtils.initVodClient(ConstantPropertiesUtil.ACCESS_KEY_ID, ConstantPropertiesUtil.ACCESS_KEY_SECRET);

            //2.创建请求对象、响应对象（根据操作不同，选择不同的类）
            GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
            GetVideoPlayAuthResponse response = new GetVideoPlayAuthResponse();
            //3.把参数传入请求对象
            request.setVideoId(videoId);
            //4.调用初始化对象 获取响应
            response = client.getAcsResponse(request);
            //5.从响应中获取响应信息
            //播放凭证
            playAuth = response.getPlayAuth();
            System.out.print("PlayAuth = " + response.getPlayAuth() + "\n");
            //VideoMeta信息
            System.out.print("VideoMeta.Title = " + response.getVideoMeta().getTitle() + "\n");
        } catch (ClientException e) {
            e.printStackTrace();
            throw new GuliException(20001, "获取视频失败");
        }

        return R.ok().data("playAuth", playAuth);
    }
}
