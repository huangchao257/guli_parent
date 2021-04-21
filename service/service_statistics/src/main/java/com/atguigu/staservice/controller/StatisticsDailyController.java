package com.atguigu.staservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.staservice.service.StatisticsDailyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-04-20
 */
@Api(description = "统计分析")
//@CrossOrigin
@RestController
@RequestMapping("/staservice/daily")
public class StatisticsDailyController {

    @Autowired
    private StatisticsDailyService dailyService;

    @ApiOperation("生成统计数据")
    @PostMapping("createStaDaily/{day}")
    public R createStaDaily(@PathVariable String day) {
        dailyService.createStaDaily(day);
        return R.ok();
    }

    @ApiOperation("查询统计数据")
    @GetMapping("showChart/{type}/{begin}/{end}")
    public R showChart(@PathVariable String type,@PathVariable String begin,@PathVariable String end){
        Map<String, Object> map = dailyService.showChart(type, begin, end);
        return R.ok().data(map);
    }

}

