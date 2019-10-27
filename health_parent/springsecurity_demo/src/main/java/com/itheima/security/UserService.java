package com.itheima.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserService implements UserDetailsService {
    public static BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();

    //模拟数据库中的用户数据
    public static Map<String,com.itheima.health.pojo.User> map=new HashMap<>();
    static {
        com.itheima.health.pojo.User user1=new com.itheima.health.pojo.User();
        user1.setUsername("admin");
//        user1.setPassword("admin");
        user1.setPassword(encoder.encode("admin"));



        com.itheima.health.pojo.User user2=new  com.itheima.health.pojo.User();
        user2.setUsername("zhangSan");
//        user2.setPassword("123");
          user2.setPassword(encoder.encode("123"));

        map.put(user1.getUsername(),user1);
        map.put(user2.getUsername(),user2);


    }




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("username:"+username);
        com.itheima.health.pojo.User userInDb=map.get(username);
        if (userInDb==null){
            //根据用户名 没有查询到用户，抛出异常，表示登录名输入有误
            return null;
        }
        //模拟数据库中的密码，后期需要查询数据库
//        String passwordInDb = "{noop}" + userInDb.getPassword();
        String passwordInDb = userInDb.getPassword();

        List<GrantedAuthority>list=new ArrayList<>();
        //授权，后期需要改善查询数据库动态获得用户拥有的权限和角色
        list.add(new SimpleGrantedAuthority("add"));//权限
        list.add(new SimpleGrantedAuthority("delete"));//权限
        list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));//角色

        //返回true，参数一：存放登录名，参数二：存放数据库查询的密码（数据库获取的密码，默认会和页面获取的进行对比成功跳转到成功页面，失败回到登录页面，并抛出异常表示失败），存放当前用户具有的角色

        return  new User(userInDb.getUsername(),passwordInDb,list);
    }
}
