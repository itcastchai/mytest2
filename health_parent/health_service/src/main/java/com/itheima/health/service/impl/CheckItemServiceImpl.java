package com.itheima.health.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service(interfaceClass= CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    CheckItemDao checkItemDao;
    @Autowired
    private JedisPool jedisPool;
    @Override
    public void add(CheckItem checkItem) {

        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult queryPage(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckItem> page=checkItemDao.selectQueryString(queryString);
        return new PageResult(page.getTotal(),page.getResult())   ;
    }

    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    @Override
    public void deleteId(Integer id)throws RuntimeException{
        //查询当前检查项是否和检查组关联
        long count=checkItemDao.findCountDeleteId(id);
        if (count>0){
            //当前检查项被引用，不能删除
            throw new RuntimeException("当前检查项被检查组应用，不能删除");
        }
        checkItemDao.deleteId(id);

    }

    @Override
    public List<CheckItem> findAll() {

        List<CheckItem>list=new ArrayList<>();

        //1.获取缓存中所有数据
        Set<String> keys = jedisPool.getResource().keys("checkItems*");
        if (keys==null||keys.size()==0){
            List<CheckItem>checkItemList=checkItemDao.findAll();
            for (CheckItem checkItem :checkItemList) {
                String  checkItems= JSONObject.toJSONString(checkItem);
                jedisPool.getResource().set("checkItems"+checkItem.getId(),checkItems);
               CheckItem checkItem1 = JSONObject.parseObject(checkItems, CheckItem.class);
                list.add(checkItem1);

            }
        }else {
            //若缓存中不为空则从缓存中获取数据
            for (String key : keys) {

                String s = jedisPool.getResource().get(key);
               CheckItem checkItem  = JSONObject.parseObject(s, CheckItem.class);
                list.add( checkItem);

            }
        }

        return  list;
    }
}
