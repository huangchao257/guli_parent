package com.atguigu.orderservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.commonutils.utils.JwtUtils;
import com.atguigu.orderservice.entity.TOrder;
import com.atguigu.orderservice.service.TOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-04-17
 */
@Api(description = "订单管理")
//@CrossOrigin
@RestController
@RequestMapping("/orderservice/order")
public class TOrderController {

    @Autowired
    private TOrderService orderService;

    @ApiOperation("创建订单")//根据课程id和用户id（通过token字符串获取）创建订单，返回订单编号
    @PostMapping("createOrder/{courseId}")
    public R createOrder(@PathVariable String courseId, HttpServletRequest request){
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        String orderNo = orderService.saveOrder(courseId, memberId);
        return R.ok().data("orderNo", orderNo);
    }

    //根据订单id获取订单信息
    @ApiOperation("根据订单编号获取订单信息")
    @GetMapping("getOrderByNo/{orderNo}")
    public R getOrderByNo(@PathVariable String orderNo){
        TOrder order = orderService.getOne(new QueryWrapper<TOrder>().eq("order_no", orderNo));
        return R.ok().data("order", order);
    }

    @ApiOperation("根据课程id、用户id查询订单是否支付")
    @GetMapping("isBuyCourse/{memberId}/{courseId}")
    public boolean isBuyCourse(@PathVariable("memberId") String memberId, @PathVariable("courseId") String courseId) {
        QueryWrapper<TOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId);
        wrapper.eq("course_id", courseId);
        wrapper.eq("status", 1);
        int count = orderService.count(wrapper);
        return count == 0 ? false : true;
    }
}

