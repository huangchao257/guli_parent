package com.atguigu.wxlogin.controller;


import com.atguigu.commonutils.utils.JwtUtils;
import com.atguigu.servicebase.handler.GuliException;
import com.atguigu.wxlogin.entity.UcenterMember;
import com.atguigu.wxlogin.service.UcenterMemberService;
import com.atguigu.wxlogin.utils.ConstantPropertiesUtil;
import com.atguigu.wxlogin.utils.HttpClientUtils;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-04-14
 */
@Api(description = "微信扫码登录")
//@CrossOrigin
@Controller
@RequestMapping("/api/ucenter/wx")
public class UcenterMemberController {

    @Autowired
    private UcenterMemberService memberService;

    @ApiOperation("生成微信授权登录二维码")
    @GetMapping("login")
    public String login(){
        //拼写重定向地址
        // https://open.weixin.qq.com/connect/qrconnect?
        // appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
        // 微信开放平台授权baseUrl
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        // 回调地址
        String redirectUrl = ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL;//获取业务服务器重定向地址
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");//url编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //优化：采用redis等进行缓存state 使用sessionId为key 30分钟后过期，可配置
        //键："wechar-open-state-" + httpServletRequest.getSession().getId()
        //值：satte
        //过期时间：30分钟
        //生成qrcodeUrl

        String qrcodeUrl = String.format(
                baseUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                redirectUrl,
                "wxatguigu");
        return "redirect:" + qrcodeUrl;
    }

    @GetMapping("callback")
    public String callback(String code, String state, HttpSession session) {
        //得到授权临时票据code
        System.out.println("code = " + code);
        System.out.println("state = " + state);

        //从redis中将state获取出来，和当前传入的state作比较
        //如果一致则放行，如果不一致则抛出异常：非法访问

        //向认证服务器发送请求换取access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";
        String accessTokenUrl = String.format(baseAccessTokenUrl, ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET, code);
        //解析json字符串
        String result = null;
        try {
            result = HttpClientUtils.get(accessTokenUrl);
            System.out.println("result = " + result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GuliException(20001,"获取access_token失败");
        }
        Gson gson = new Gson();
        HashMap map = gson.fromJson(result, HashMap.class);
        String accessToken = (String) map.get("access_token");
        String openid = (String) map.get("openid");
        System.out.println("accessToken = " + accessToken);
        System.out.println("openid = " + openid);

        UcenterMember member = memberService.getByOpenId(openid);
        if (member == null) {
            //根据access_token，获取用户信息
            //访问微信的资源服务器，获取用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
            String userInfo = null;
            try {
                userInfo = HttpClientUtils.get(userInfoUrl);
            } catch (Exception e) {
                e.printStackTrace();
                throw new GuliException(20001, "获取用户信息失败");
            }
            HashMap<String, Object> mapUserInfo = gson.fromJson(userInfo, HashMap.class);

            String nickname = (String)mapUserInfo.get("nickname");
            String headimgurl = (String)mapUserInfo.get("headimgurl");

            //向数据库中插入一条记录
            //优化：将用户头像转存到平台的oss服务中
            member = new UcenterMember();
            member.setNickname(nickname);
            member.setOpenid(openid);
            member.setAvatar(headimgurl);
            member.setIsDisabled(false);
            memberService.save(member);
        }
        //使用JWT生成登录后token
        String token = JwtUtils.getJwtToken(member.getId(), member.getNickname());

        return "redirect:http://localhost:3000?token=" + token;
    }

}

