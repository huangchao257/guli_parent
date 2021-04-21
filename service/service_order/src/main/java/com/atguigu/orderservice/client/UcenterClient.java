package com.atguigu.orderservice.client;

import com.atguigu.servicebase.vo.UcenterMemberForPay;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("service-ucenter")
public interface UcenterClient {

    //根据用户id获取用户信息
    @GetMapping("/ucenterservice/member/getUcenterForPay/{memberId}")
    public UcenterMemberForPay getUcenterForPay(@PathVariable("memberId") String memberId);
}
