package com.atguigu.ucenterservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.commonutils.utils.JwtUtils;
import com.atguigu.servicebase.vo.UcenterMemberForPay;
import com.atguigu.ucenterservice.entity.UcenterMember;
import com.atguigu.ucenterservice.entity.vo.LoginInfoVo;
import com.atguigu.ucenterservice.entity.vo.LoginVo;
import com.atguigu.ucenterservice.entity.vo.RegisterVo;
import com.atguigu.ucenterservice.service.UcenterMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-04-13
 */
@Api(description = "前台会员管理")
//@CrossOrigin
@RestController
@RequestMapping("/ucenterservice/member")
public class UcenterMemberController {

    @Autowired
    private UcenterMemberService memberService;

    @ApiOperation("用户注册")
    @PostMapping("register")
    public R register(@RequestBody RegisterVo registerVo) {
        memberService.register(registerVo);
        return R.ok();
    }

    @ApiOperation("用户登录")
    @PostMapping("login")
    public R login(@RequestBody LoginVo loginVo) {
        String token = memberService.login(loginVo);
        return R.ok().data("token",token);
    }

    @ApiOperation("根据token获取用户信息")
    @GetMapping("getMemberByToken")
    public R getMemberByToken(HttpServletRequest request) {
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        LoginInfoVo member = memberService.getLoginVo(memberId);
        return R.ok().data("member", member);
    }

    @ApiOperation("根据用户id获取用户信息远程调用")
    @GetMapping("getUcenterForPay/{memberId}")
    public UcenterMemberForPay getUcenterForPay(@PathVariable("memberId") String memberId){
        UcenterMember member = memberService.getById(memberId);
        UcenterMemberForPay ucenterMemberForPay = new UcenterMemberForPay();
        BeanUtils.copyProperties(member, ucenterMemberForPay);
        return ucenterMemberForPay;
    }

    @ApiOperation("根据日期统计注册信息")
    @GetMapping("registerCount/{day}")
    public R registerCount(@PathVariable String day) {
        Integer registerCount = memberService.registerCount(day);
        return R.ok().data("registerCount", registerCount);
    }
}

