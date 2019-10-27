package com.itheima.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping(value = "/add")
    @PreAuthorize(value = "hasAuthority('add')")
    public String add(){
        System.out.println("add...");
        return "add";
    }

    @RequestMapping(value = "/find")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    public String find(){
        System.out.println("find...");
        return "find";
    }

    @RequestMapping(value = "/delete")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public String delete(){
        System.out.println("delete...");
        return "delete";
    }


}
