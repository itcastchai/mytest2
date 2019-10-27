package com.itheima.health.job;

import com.itheima.health.constant.RedisConstant;
import com.itheima.health.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Iterator;
import java.util.Set;

/*定时任务：清理垃圾图片
 * */
public class ClearImgJob {

    @Autowired
    private JedisPool jedisPool;
    public void  clearImg(){
        //计算两个集合的差值，获取垃圾图片名称
        Set<String>set=jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES,RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        Iterator<String> iterator=set.iterator();
        while (iterator.hasNext()) {
            String pic=iterator.next();
            System.out.println("删除图片的名称是："+pic);
            QiniuUtils.deleteFileFromQiniu(pic);
            //删除redis中的数据
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,pic);
        }
    }

}
