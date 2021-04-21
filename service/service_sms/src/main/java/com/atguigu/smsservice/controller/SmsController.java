package com.atguigu.smsservice.controller;

import com.atguigu.commonutils.R;
import com.atguigu.commonutils.utils.RandomUtil;
import com.atguigu.smsservice.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(description = "短信服务")
//@CrossOrigin
@RestController
@RequestMapping("/edusms/send")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @ApiOperation(value = "发送验证码")
    @GetMapping(value = "sendCode/{phone}")
    public R sendCode(@PathVariable String phone){
        //1.从redis中获取验证码
        String code = redisTemplate.opsForValue().get(phone);
        //2.判断是否发送过短信,发送过直接返回
        if(!StringUtils.isEmpty(code)){
            return R.ok();
        }
        //3.没有则生成验证码
        code = RandomUtil.getFourBitRandom();
        //4.封装调用接口参数
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        //5.发送验证码,获取返回值
        boolean isSend = smsService.sendCode(phone, map);
        //6.将验证码存入redis并设置失效时间
        if (isSend){
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.ok();
        }else {
            return R.error().message("验证码发送失败");
        }
    }

}
