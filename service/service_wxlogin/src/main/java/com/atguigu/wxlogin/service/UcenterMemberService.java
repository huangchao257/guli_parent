package com.atguigu.wxlogin.service;

import com.atguigu.wxlogin.entity.UcenterMember;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author hc
 * @since 2021-04-14
 */
public interface UcenterMemberService extends IService<UcenterMember> {

    UcenterMember getByOpenId(String openid);
}
