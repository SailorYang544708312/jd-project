package com.jd.sellergoods.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

//专门用于springSecurity的支持
@RestController
@RequestMapping("login")
public class LoginController {

   @RequestMapping("name")
   public Map<String,String> name(){
      //从springSecurity中去取当前登录用户名
      String name = SecurityContextHolder.getContext().getAuthentication().getName();
      Map<String, String> map = new HashMap<>();
      map.put("loginName",name);
      return map;
   }
}
