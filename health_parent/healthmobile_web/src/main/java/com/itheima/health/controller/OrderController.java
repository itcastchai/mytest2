package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Order;
import com.itheima.health.service.OrderService;
import com.itheima.health.utils.DateUtils;
import com.itheima.health.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;
    @Autowired
    private JedisPool jedisPool;
    @RequestMapping("/submit")
    public Result submitOrder(@RequestBody Map map) throws ClientException {
         //获取提交的电话号码
        String telephone = (String) map.get("telephone");
        //获取提交的验证码
        String validateCode= (String) map.get("validateCode");
        //获取缓存中验证码
        String redisCode=jedisPool.getResource().get(telephone+ RedisMessageConstant.SENDTYPE_ORDER);
        //1.校验验证码是否正确
        if (validateCode==null||!validateCode.equals(redisCode)) {
            //验证码不同
            return new Result(false,MessageConstant.VALIDATECODE_ERROR);
        }

        //设置预约类型
        map.put("orderType",Order.ORDERTYPE_WEIXIN);
        Result result=null;
        try {
            result = orderService.oder(map);

        } catch (Exception e) {
            e.printStackTrace();
            //预约失败
                  return result;

        }

        if (result.isFlag()){


            String orderDate= (String) map.get("orderDate");

            try {
                //预约成功，发送短信
                SMSUtils.sendShortMessage(telephone,orderDate);
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
        //预约成功
        return result;
    }
    @RequestMapping("/findById")
    public Result findById(@RequestParam Integer id) throws Exception {
       //通过预约id获取一条数据得到会员id及套餐id最终获取（会员中）体检人,(套餐)体检套餐，(预约)预约日期,预约类型,封装成Map集合

        try {
            Map map = orderService.findById(id);
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
        }

         return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
    }


}
