package com.atguigu.orderservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.orderservice.entity.TOrder;
import com.atguigu.orderservice.entity.TPayLog;
import com.atguigu.orderservice.mapper.TPayLogMapper;
import com.atguigu.orderservice.service.TOrderService;
import com.atguigu.orderservice.service.TPayLogService;
import com.atguigu.orderservice.utils.HttpClient;
import com.atguigu.servicebase.handler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-04-17
 */
@Service
public class TPayLogServiceImpl extends ServiceImpl<TPayLogMapper, TPayLog> implements TPayLogService {

    @Autowired
    private TOrderService orderService;

    @Override
    public Map createNative(String orderNo) {
        try {
            //1.根据订单编号获取订单信息
            TOrder order = orderService.getOne(new QueryWrapper<TOrder>().eq("order_no", orderNo));
            if (order == null) {
                throw new GuliException(20001, "订单失效");
            }
            //2.设置支付订单参数
            Map m = new HashMap();
            m.put("appid", "wx74862e0dfcf69954");//关联的公众号appid
            m.put("mch_id", "1558950191");//商户号
            m.put("nonce_str", WXPayUtil.generateNonceStr());//随机号
            m.put("body", order.getCourseTitle());//商品名称
            m.put("out_trade_no", orderNo);//商户订单号
            m.put("total_fee", order.getTotalFee().multiply(new BigDecimal("100")).longValue()+"");//支付金额
            m.put("spbill_create_ip", "127.0.0.1");//设备ip地址
            m.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify\n");//回调地址
            m.put("trade_type", "NATIVE");//支付类型
            //3.HttpClient来根据URL访问第三方接口并且传递参数(xml)
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //设置参数
            client.setXmlParam(WXPayUtil.generateSignedXml(m,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));//key:商户key
            client.setHttps(true);//开启https协议
            client.post();//以post方式发送
            //4.获取返回结果，使用工具转化xml
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //5.再次封装
            Map map = new HashMap<>();
            map.put("out_trade_no", orderNo);
            map.put("course_id", order.getCourseId());
            map.put("total_fee", order.getTotalFee());
            map.put("result_code", resultMap.get("result_code"));//业务状态码
            map.put("code_url", resultMap.get("code_url"));//二维码链接

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new GuliException(20001, "生成支付二维码失败");
        }
    }

    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        try {
            //1.封装参数
            Map m = new HashMap<>();
            m.put("appid", "wx74862e0dfcf69954");
            m.put("mch_id", "1558950191");
            m.put("out_trade_no", orderNo);
            m.put("nonce_str", WXPayUtil.generateNonceStr());
            //2.HttpClient来根据URL访问第三方接口并且传递参数(xml)
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(m, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            client.post();
            //3.获取返回结果，使用工具转化xml为map
            String xml = client.getContent();
            System.out.println("xml = " + xml);
            Map<String, String> map = WXPayUtil.xmlToMap(xml);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new GuliException(20001, "查询支付状态失败");
        }
    }

    @Override
    public void updateOrderStatus(Map<String, String> map) {
        //1.获取订单编号
        String orderNo = map.get("out_trade_no");
        //2.查询订单信息
        TOrder order = orderService.getOne(new QueryWrapper<TOrder>().eq("order_no", orderNo));
        if (order.getStatus().intValue()==1) return;//如果已支付，直接结束方法
        //3.修改订单状态
        order.setStatus(1);
        orderService.updateById(order);
        //4.插入支付日志
        TPayLog payLog = new TPayLog();
        payLog.setOrderNo(order.getOrderNo());//支付订单号
        payLog.setPayTime(new Date());
        payLog.setPayType(1);//支付类型
        payLog.setTotalFee(order.getTotalFee());//总金额(分)
        payLog.setTradeState(map.get("trade_state"));//支付状态
        payLog.setTransactionId(map.get("transaction_id"));
        payLog.setAttr(JSONObject.toJSONString(map));//备份微信返回的信息
        baseMapper.insert(payLog);
    }
}
