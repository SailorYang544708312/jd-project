package com.jd.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("login")
public class LoginController {

   /**
    * 专门用于springSecurity支持
    * @return
    */
   @RequestMapping("name")
   public Map<String,String> name(){
      //从springSecurity中取出当前配置登录的用户
      String name = SecurityContextHolder.getContext().getAuthentication().getName();
      Map<String, String> map = new HashMap<String, String>();
      map.put("loginName",name);
      return map;
   }
}
