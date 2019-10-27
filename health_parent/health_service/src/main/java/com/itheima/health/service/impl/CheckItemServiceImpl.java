package com.itheima.health.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceClass= CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    CheckItemDao checkItemDao;
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
         return checkItemDao.findAll();
    }
}
