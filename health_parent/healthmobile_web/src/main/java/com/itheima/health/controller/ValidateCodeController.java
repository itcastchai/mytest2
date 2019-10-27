package com.itheima.health.controller;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.utils.SMSUtils;
import com.itheima.health.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
    @Autowired
    private JedisPool jedisPool;

    @RequestMapping("/send4Order")
    public Result send4Order (@RequestParam String telephone) throws Exception {
        //1.随机生成4位数验证码
        Integer code= ValidateCodeUtils.generateValidateCode(4);

        System.out.println(code);

        try {
            //2.短信发送生成验证码
            SMSUtils.sendShortMessage(telephone,code.toString());
        } catch (ClientException e) {
            e.printStackTrace();
            //3.获取短信验证码失败
            new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        //将短信验证码存入缓存中
        jedisPool.getResource().setex(telephone+RedisMessageConstant.SENDTYPE_ORDER,5*60*60,code.toString());
       //发送短信验证码成功
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);


    }


    @RequestMapping("/send4Login")
    public Result send4Login (@RequestParam String telephone) throws Exception {
        //1.随机生成4位数验证码
        Integer code = ValidateCodeUtils.generateValidateCode(4);
        System.out.println(code);
        //手机发送短信
        try {
            SMSUtils.sendShortMessage(telephone,code.toString());
        } catch (ClientException e) {
            e.printStackTrace();
            //获取手机短信验证码失败
            return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        //验证码发送成功
        //将短信验证码存入缓存中
        jedisPool.getResource().setex(telephone+RedisMessageConstant.SENDTYPE_LOGIN ,24*60*60,code.toString());
        //发送短信验证码成功
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);



    }

}
