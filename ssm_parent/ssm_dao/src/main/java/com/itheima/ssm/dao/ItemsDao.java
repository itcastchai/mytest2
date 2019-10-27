package com.itheima.ssm.dao;

import com.itheima.ssm.domain.Items;

import java.util.List;


public interface ItemsDao {


    /***
     * 集合查询
     * @return
     */
    List<Items> list();

    /***
     * 保存操作
     * @param items
     * @return
     */
    int save(Items items);
}
