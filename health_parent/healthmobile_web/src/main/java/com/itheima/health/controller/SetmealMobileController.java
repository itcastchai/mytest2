package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealMobileController {
    @Reference
    private SetmealService setmealService;
    @Autowired
    private JedisPool jedisPool;

//   查询所有套餐数据
    @RequestMapping("/getSetmeal")
    public Result getSetmeal(){
        try {
            //获取缓存中的数据
        List<Setmeal>setmealList=setmealService.findAll();


        return new Result(true,MessageConstant.GET_SETMEAL_LIST_SUCCESS,setmealList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_SETMEAL_LIST_FAIL);
        }
    }

//    根据套餐id获取套餐信息
@RequestMapping("/findById")
public Result findById(@RequestParam Integer id){
    try {
        String setmeal=setmealService.findByIdSetmeal(id);
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
    } catch (Exception e) {
        e.printStackTrace();
        return new Result(false, MessageConstant.GET_SETMEAL_LIST_FAIL);
    }
}



}
