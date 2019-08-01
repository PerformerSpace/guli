package com.guli.ucenter.controller.api;

import com.google.gson.Gson;
import com.guli.common.constants.ResultCodeEnum;
import com.guli.common.exception.GuliException;
import com.guli.common.vo.R;
import com.guli.ucenter.entity.Member;
import com.guli.ucenter.service.MemberService;
import com.guli.ucenter.util.ConstantPropertiesUtil;
import com.guli.ucenter.util.HttpClientUtils;
import com.guli.ucenter.util.JwtUtils;
import com.guli.ucenter.vo.LoginInfoVo;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Component
@RequestMapping("/api/ucenter/wx")
public class WxApiController {

    @Autowired
    private MemberService memberService;

    @GetMapping("login")
    public String genQrConnect(HttpSession session){
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        // 回调地址
        String redirectUri = ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL;
        try {
            redirectUri = URLEncoder.encode(redirectUri,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new GuliException(ResultCodeEnum.URL_ENCODE_ERROR);
        }

        // 防止csrf攻击（跨站请求伪造攻击）
        //String state = UUID.randomUUID().toString().replaceAll("-", "");//一般情况下会使用一个随机数
        String state = "jiangyf";
        session.setAttribute("wx-open-state", state);
        //生成qrcodeUrl
        String qrconnectUrl = String.format(
                baseUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                redirectUri,
                state);

        return "redirect:"+ qrconnectUrl;

    }

    @GetMapping("callback")
    public String callback(String code, String state, HttpSession session){

        System.out.println("sessionId = " + session.getId());

        //得到授权临时票据code
        System.out.println("code = " + code);
        System.out.println("获取 state = " + state);

        // 判断state是否合法
        String stateStr = (String)session.getAttribute("wx-open-state");
        System.out.println("存储 state = " + stateStr);

        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(stateStr) || !state.equals(stateStr)){

            throw new GuliException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        // 通过code获取access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";

        String accessTokenUrl = String.format(
                baseAccessTokenUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);

        String result = null;
        try {
            result = HttpClientUtils.get(accessTokenUrl);
        } catch (Exception e) {
            throw new GuliException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        Gson gson = new Gson();
        HashMap<String, Object> resultMap = gson.fromJson(result, HashMap.class);
        if (resultMap.get("errcode") != null) {
            throw new GuliException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        String accessToken = (String) resultMap.get("access_token");
        String openid = (String) resultMap.get("openid");

        if (StringUtils.isEmpty(accessToken) || StringUtils.isEmpty(openid)) {
            throw new GuliException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        // 根据Openid返回用户信息。
        Member member = memberService.getByOpenid(openid);

        if (member == null) { //新用户

            System.out.println("新用户注册");

            //从微信获取用户信息
            //获取用户基本信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);

            //获取用户信息
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println(resultUserInfo);
            } catch (Exception e) {
                throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }

            Map<String, Object> resultUserInfoMap = gson.fromJson(resultUserInfo, HashMap.class);

            if (resultMap.get("errcode") != null) {
                throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }

            //保存用户信息
            String nickname = (String)resultUserInfoMap.get("nickname");
            String headimgurl = (String)resultUserInfoMap.get("headimgurl");

            // 将用户信息存入数据库
            member = new Member();
            member.setNickname(nickname);
            member.setOpenid(openid);
            member.setAvatar(headimgurl);
            memberService.save(member);

        }

        //登录
        String jwtToken = JwtUtils.generateJWT(member);


        return "redirect:http://localhost:3000?token=" + jwtToken;


    }

    @PostMapping("parse-jwt")
    @ResponseBody
    public R getLoginInfoByJwtToken(@RequestBody String jwtToken){

        Claims claims = JwtUtils.checkJWT(jwtToken);
        String id = (String)claims.get("id");
        String nickname = (String)claims.get("nickname");
        String avatar = (String)claims.get("avatar");

        LoginInfoVo loginInfoVo = new LoginInfoVo();
        loginInfoVo.setId(id);
        loginInfoVo.setNickname(nickname);
        loginInfoVo.setAvatar(avatar);

        return R.ok().data("loginInfo", loginInfoVo);

    }
}
