package com.itheima.health.dao;

import com.itheima.health.pojo.Role;

import java.util.List;
import java.util.Set;

public interface RoleDao {

    Set<Role> findByUserId(Integer id);

}
