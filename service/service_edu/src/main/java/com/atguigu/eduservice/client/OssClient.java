package com.atguigu.eduservice.client;

import com.atguigu.commonutils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("service-oss")
public interface OssClient {

    @DeleteMapping("/eduoss/fileoss/delFile")
    public R delFile(@RequestBody String cover);
}
