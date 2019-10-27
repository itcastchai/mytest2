package com.itheima.health.service.impl;

import com.alibaba.druid.sql.visitor.functions.If;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.constant.RedisConstant;
import com.itheima.health.dao.SetmealDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private  JedisPool jedisPool;
    
    @Override
    public void handleAdd(Integer[] checkgroupIds, Setmeal setmeal) {
        //在Setmeal插入一条数据
        setmealDao.handleAdd(setmeal);
        //获取党前插入表中数据的id
        Integer setmealId=setmeal.getId();
        //调用方法更新关联表中的数据
        updateAssouciationCheckgroupAndSetmeal(checkgroupIds, setmealId);
        if (setmeal.getImg()!=null) {
            //新增图片不为空则保存图片到缓存中
            //将图片名称保存到Redis
            savaPic2Redis(setmeal.getImg());
        }

    }
    //将图片名称保存到Redis
    public void savaPic2Redis(String pic){
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,pic);

    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        Page<Setmeal>page=setmealDao.findPage(queryPageBean.getQueryString());

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Integer> findById(Integer id) {
        return setmealDao. findById(id);
    }

    @Override
    public Setmeal findSetmealById(Integer id) {
       return setmealDao.findSetmealById(id);

    }
//修改数据
    @Override
    public void edit(Setmeal setmeal, Integer[] checkgroupIds) {

        //1.根据id获取套餐数据库原有图片
        String odlImg= setmealDao.findImg(setmeal.getId());
           String setmealImg=setmeal.getImg();
        if (odlImg!=null&&!odlImg.equals(setmealImg)){
            //不相同就清除旧照片
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES, odlImg);


        }

        //修改数据库照片
        if (setmeal.getImg()!=null){

            //修改图片不为空就进行下一步
            //将修改的图片保存到缓存中存的是更新过后的数据库图片
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());


        }
        //查询当前id对应的检查组的关系
        Integer setmealId = setmeal.getId();
        Integer count=setmealDao.findSetmealAndCkgroupAssociation(setmealId);

        if (count>0){

            //2.先删除关联表数据中的
            setmealDao.deleteSetmealAndCkgroupAssociation( setmealId );

            //3.更新关联表数据
            reSettingSetmealAndCheckgroup(setmealId,checkgroupIds);
        }
        //4.修改套餐表数据
        setmealDao.edit(setmeal);


    }

    public void reSettingSetmealAndCheckgroup(Integer setmealId,Integer[]checkgroupIds){

        if (checkgroupIds!=null&&checkgroupIds.length>0){

            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.insertSetmealAndCheckgroupAssociation(setmealId,checkgroupId);


            }
        }

    }
//删除
    @Override
    public void handleDelete(Integer id) throws RuntimeException{
        //1.查询关联表是否有相关联数据
       long count= setmealDao.selectAssouciationById(id);
        if (count>0) {
           throw new RuntimeException("有关联数据不能删除");
        }else {
            setmealDao.handleDelete(id);

        }
       

    }
//查询所有套餐
    @Override
    public String  findAll(){
        //获取缓存
        String setmeals =jedisPool.getResource().get("setmeals");

        if (setmeals==null||"".equals(setmeals)) {

            List<Setmeal>setmealList=setmealDao.findAll();
            if (setmealList!=null&&setmealList.size()>0) {
                setmeals= JSONObject.toJSONString(setmealList);
                jedisPool.getResource().sadd("setmeals",setmeals);
            }else {
                return null;
            }

        }


        return  setmeals ;


    }

    @Override
    public String findByIdSetmeal(Integer id) {

        //获取缓存
        String setmeal =jedisPool.getResource().get("setmeal");


        if (setmeal==null||"".equals(setmeal)) {

            setmeal=setmealDao.findByIdSetmeal(id);
            if (setmeal!=null&&setmeal.length()>0) {
                setmeal= JSONObject.toJSONString(setmeal);
                jedisPool.getResource().sadd("setmeal",setmeal);
            }else {
                return null;
            }

        }

        return setmeal;
    }


    public void  updateAssouciationCheckgroupAndSetmeal(Integer[]checkgroupIds,Integer setmealId){
        if (checkgroupIds!=null&&checkgroupIds.length>0) {
            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.insertSetmealIdAndcheckgroupId(setmealId,checkgroupId);

            }
        }
    }
}
