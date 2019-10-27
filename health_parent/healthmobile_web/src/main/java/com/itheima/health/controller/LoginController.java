package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Reference
    private MemberService memberService;
    @Autowired
    public JedisPool jedisPool;
    @RequestMapping("/check")
    public Result check(HttpServletResponse response, @RequestBody Map map){
        //获取手机端提交的手机号及验证码
        String telephone = (String) map.get("telephone");
        //获取验证码
        String validateCode = (String) map.get("validateCode");
        //获取缓存中的验证码
        String code=jedisPool.getResource().get(telephone+ RedisMessageConstant.SENDTYPE_LOGIN);

        if (code==null||!code.equals(validateCode)) {
            //验证码验证不通过
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }else {
            //判断是否是会员
            Member member=memberService.findByTelephone(telephone);
            if (member==null) {
                //不是会员则自动注册会员
                Member member1=new Member();
                member1.setPhoneNumber(telephone);
                member1.setRegTime(new Date());
                memberService.addMember(member1);
            }
            //登录成功，存入cookie，有效期30天
            Cookie cookie=new Cookie("login_member_telephone",telephone);
            cookie.setPath("/");
            cookie.setMaxAge(60*60*24*30);//有效期30天
            response.addCookie(cookie);
            //登录成功
            return new Result(true,MessageConstant.LOGIN_SUCCESS);
        }






    }


}
