package com.jd.shop.security;

import com.alibaba.dubbo.common.json.JSON;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
   @Override
   public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException, ServletException {
      Map<String, Object> map = new HashMap<>(2);
      map.put("success",false);
      //Bad credentials:密码错误
      if ("Bad credentials".equals(ex.getMessage())){
         map.put("message","密码错误");
      }else {
         map.put("message",ex.getMessage());
      }
      String result = JSON.json(map);
      response.setContentType("text/json;charset=utf-8");
      response.getWriter().write(result);
   }
}
