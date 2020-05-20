package com.jd.user.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("login")
public class LoginController {

   /**
    * 查询当前登录的用户名(有可能是普通请求，也有可能是js的跨域请求 区别在于有没有callback)
    * @return
    */
   @RequestMapping(value = "name",produces = "application/javaScript;charset=UTF-8")
   public Object showName(String callback){
      Map<String, String> map = new HashMap<>();
      //获取登录的姓名
      String name = SecurityContextHolder.getContext().getAuthentication().getName();
      map.put("loginName",name);
      if (callback != null && !"".equals(callback)){
         //表示是jsonp请求
         return callback + "("+ JSON.toJSONString(map) +")";

      }
      return map;
   }
}
