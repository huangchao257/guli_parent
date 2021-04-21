package com.atguigu.orderservice.service.impl;

import com.atguigu.orderservice.client.EduClient;
import com.atguigu.orderservice.client.UcenterClient;
import com.atguigu.orderservice.entity.TOrder;
import com.atguigu.orderservice.mapper.TOrderMapper;
import com.atguigu.orderservice.service.TOrderService;
import com.atguigu.orderservice.utils.OrderNoUtil;
import com.atguigu.servicebase.handler.GuliException;
import com.atguigu.servicebase.vo.CourseWebVoForPay;
import com.atguigu.servicebase.vo.UcenterMemberForPay;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-04-17
 */
@Service
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements TOrderService {

    @Autowired
    private EduClient eduClient;

    @Autowired
    private UcenterClient ucenterClient;

    @Override
    public String saveOrder(String courseId, String memberId) {
        //1.生成订单编号
        String orderNo = OrderNoUtil.getOrderNo();
        //2.根据课程id获取课程信息
        CourseWebVoForPay courseInfoForPay = eduClient.getCourseInfoForPay(courseId);
        if (courseInfoForPay == null){
            throw new GuliException(20001, "获取课程信息失败");
        }
        //3.根据用户id获取用户信息
        UcenterMemberForPay ucenterForPay = ucenterClient.getUcenterForPay(memberId);
        if (ucenterForPay == null){
            throw new GuliException(20001, "获取用户信息失败");
        }
        //4.完善订单信息
        TOrder order = new TOrder();
        order.setOrderNo(orderNo);
        order.setCourseId(courseId);
        order.setCourseTitle(courseInfoForPay.getTitle());
        order.setCourseCover(courseInfoForPay.getCover());
        order.setTeacherName(courseInfoForPay.getTeacherName());
        order.setTotalFee(courseInfoForPay.getPrice());
        order.setMemberId(memberId);
        order.setMobile(ucenterForPay.getMobile());
        order.setNickname(ucenterForPay.getNickname());
        order.setStatus(0);
        order.setPayType(1);
        baseMapper.insert(order);
        return orderNo;
    }
}
