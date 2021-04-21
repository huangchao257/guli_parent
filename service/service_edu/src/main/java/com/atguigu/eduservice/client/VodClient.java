package com.atguigu.eduservice.client;

import com.atguigu.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "service-vod",fallback = VodFileDegradeFeignClient.class)
public interface VodClient {
    @DeleteMapping("/eduvod/video/delVideo/{videoId}")
    public R removeVideo(@PathVariable String videoId);

    @DeleteMapping("/eduvod/video/delVideoBatch")
    public R removeVideoList(@RequestParam("videoIdList") List<String> videoIdList);
}
