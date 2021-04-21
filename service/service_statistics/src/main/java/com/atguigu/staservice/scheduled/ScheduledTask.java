package com.atguigu.staservice.scheduled;

import com.atguigu.staservice.service.StatisticsDailyService;
import com.atguigu.staservice.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 定时任务类
 */
@Component
public class ScheduledTask {

    @Autowired
    private StatisticsDailyService dailyService;

//    @Scheduled(cron = "0/5 * * * * ?")//没5秒执行一次
    public void testTask(){
        System.out.println("执行任务===================");
    }

    @Scheduled(cron = "0 0 1 * * ? ")
    public void task(){
        //获取上一天的日期
        String day = DateUtil.formatDate(DateUtil.addDays(new Date(), -1));
        dailyService.createStaDaily(day);
    }
}
