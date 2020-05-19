package com.jd.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("login")
public class LoginController {

   /**
    * 查询当前登录的用户名
    * @return
    */
   @RequestMapping("name")
   public Map<String,String> showName(){
      Map<String, String> map = new HashMap<>();
      //获取登录的姓名
      String name = SecurityContextHolder.getContext().getAuthentication().getName();
      map.put("loginName",name);
      return map;
   }
}
