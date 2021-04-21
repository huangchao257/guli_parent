package com.atguigu.ucenterservice.service.impl;

import com.atguigu.commonutils.utils.JwtUtils;
import com.atguigu.commonutils.utils.MD5;
import com.atguigu.servicebase.handler.GuliException;
import com.atguigu.ucenterservice.entity.UcenterMember;
import com.atguigu.ucenterservice.entity.vo.LoginInfoVo;
import com.atguigu.ucenterservice.entity.vo.LoginVo;
import com.atguigu.ucenterservice.entity.vo.RegisterVo;
import com.atguigu.ucenterservice.mapper.UcenterMemberMapper;
import com.atguigu.ucenterservice.service.UcenterMemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-04-13
 */
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void register(RegisterVo registerVo) {
        //1.验空
        String nickname = registerVo.getNickname();
        String mobile = registerVo.getMobile();
        String code = registerVo.getCode();
        String password = registerVo.getPassword();
        if (StringUtils.isEmpty(nickname)||StringUtils.isEmpty(mobile)||StringUtils.isEmpty(code)||StringUtils.isEmpty(password)){
            throw new GuliException(20001, "注册信息缺失");
        }
        //2.验证手机号是否重复
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new GuliException(20001, "该手机号已经注册");
        }
        //3.校验验证码
        String redisCode = redisTemplate.opsForValue().get(mobile);
        if (!code.equals(redisCode)) {
            throw new GuliException(20001, "验证码有误");
        }
        //4.对密码进行MD5加密
        String encryptPassword = MD5.encrypt(password);
        UcenterMember ucenterMember = new UcenterMember();
        ucenterMember.setNickname(nickname);
        ucenterMember.setMobile(mobile);
        ucenterMember.setPassword(encryptPassword);
        ucenterMember.setAvatar("https://guli-hc.oss-cn-beijing.aliyuncs.com/2021/04/02/03af3620-7756-490f-93de-8da17e579d8ffile.png");
        ucenterMember.setIsDisabled(false);
        //5.补充信息后存入数据库
        baseMapper.insert(ucenterMember);

    }

    @Override
    public String login(LoginVo loginVo) {
        //1.验空
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
            throw new GuliException(20001, "手机号或密码有误");
        }

        //2.验证用户是否存在
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        UcenterMember member = baseMapper.selectOne(wrapper);
        if (member == null){
            throw new GuliException(20001, "手机号或密码有误");
        }
        //3.验证密码是否正确
        String encryptPassword = MD5.encrypt(password);
        if (!encryptPassword.equals(member.getPassword())) {
            throw new GuliException(20001, "手机号或密码有误");
        }
        //4，生成并返回Token字符串
        String token = JwtUtils.getJwtToken(member.getId(), member.getNickname());
        return token;
    }

    @Override
    public LoginInfoVo getLoginVo(String memberId) {
        UcenterMember ucenterMember = baseMapper.selectById(memberId);
        LoginInfoVo loginInfoVo = new LoginInfoVo();
        BeanUtils.copyProperties(ucenterMember, loginInfoVo);
        return loginInfoVo;
    }

    @Override
    public Integer registerCount(String day) {
        Integer count = baseMapper.selectRegisterCount(day);
        return count;
    }

}
