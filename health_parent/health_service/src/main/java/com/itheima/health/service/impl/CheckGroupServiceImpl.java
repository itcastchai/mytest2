package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.container.page.PageHandler;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.CheckGroupService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {

   @Autowired
   private CheckGroupDao checkGroupDao;
   @Autowired
   private JedisPool jedisPool;


    //新增
    @Override
    public void handleAdd(CheckGroup checkGroup, Integer[] checkitemIds) {

        //新增CheckGroup表数据
        checkGroupDao.handleAdd(checkGroup);
        insertAssociation(checkGroup.getId(),checkitemIds);



    }
    public  void insertAssociation(Integer checkGroupId,Integer[] checkitemIds){
        //插入关联表单数据
        if (checkitemIds!=null&&checkitemIds.length>0){
            for (Integer checkitemId : checkitemIds) {
                checkGroupDao.insertCheckgroupAndCheckitem(checkGroupId,checkitemId);
            }
        }
    }


    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        Page<CheckGroup>page=checkGroupDao.findPage(queryPageBean.getQueryString());
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public CheckGroup findById(Integer id) {

     return checkGroupDao.findById(id);

    }

    @Override
    public List<Integer> findCountCheckgroupAndCheckitem(Integer id) {
        return checkGroupDao.findCountCheckgroupAndCheckitem(id);
    }

    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        //删除关联表数据
        checkGroupDao.deleteAssociation(checkGroup.getId());
       //根据id修改CheckGroup表数据
        checkGroupDao.update(checkGroup);

       //更新关联表数据
        resetting(checkGroup.getId(),checkitemIds);


    }

    @Override
    public void deleteById(Integer id) throws RuntimeException{
        long count = checkGroupDao.findAssociationById(id);
        if (count>0) {
            throw new RuntimeException("当前检查组被检查组应用，不能删除");
        }else{


            checkGroupDao.deleteById(id);
        }

    }

    @Override
    public List<CheckGroup> findAll() {

        List<CheckGroup>list=new ArrayList<>();

        //1.获取缓存中所有数据
        Set<String> keys = jedisPool.getResource().keys("CheckGroups*");
        if (keys==null||keys.size()==0){
            List<CheckGroup>checkGroupList=checkGroupDao.findAll();
            for (CheckGroup checkGroup : checkGroupList) {
                String checkGroups= JSONObject.toJSONString(checkGroup);
                jedisPool.getResource().set("checkGroups"+checkGroup.getId(),checkGroups);
                CheckGroup checkGroup1 = JSONObject.parseObject(checkGroups, CheckGroup.class);
                list.add(checkGroup1);

            }
        }else {
            //若缓存中不为空则从缓存中获取数据
            for (String key : keys) {

                String s = jedisPool.getResource().get(key);
                CheckGroup checkGroup = JSONObject.parseObject(s, CheckGroup.class);
                list.add(checkGroup);

            }
        }

        return  list;
    }

    public void resetting( Integer checkGroupId, Integer[] checkitemIds){

        for (Integer checkitemId : checkitemIds) {
            checkGroupDao.insert(checkGroupId,checkitemId);
        }

    }
}
