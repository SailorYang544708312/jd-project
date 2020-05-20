package com.jd.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.jd.cart.service.CartService;
import com.jd.common.pojo.JdResult;
import com.jd.common.utils.CookieUtil;
import com.jd.pojogroup.Cart;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("cart")
public class CartController {

   @Reference(timeout = 5000)
   private CartService cartService;

   /**
    * 展示购物车列表
    */
   @RequestMapping("findCartList")
   public List<Cart> findCartList(HttpServletRequest request){
      //获取到当前登录人账号
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      System.out.println(username);
      if (username.equals("anonymousUser")){
         //未登录
         //从cookie中获取到购物车
         String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
         //判断购物车是否存在
         if (cartListString == null || cartListString.equals("")){
            //不存在
            cartListString = "[]";
         }
         //存在,要么通过上面的代码创建一个空的，要么本身已经在cookie中有
         List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
         return cartList_cookie;
      }else {
         //已经登录
         List<Cart> cartList_redis = cartService.findCartListFormRedis(username); //从redis中取出数据
         return cartList_redis;
      }
   }


   /**
    * 添加商品到购物车
    */
   @RequestMapping("addGoodsToCartList")
   public JdResult addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response){
      String username = SecurityContextHolder.getContext().getAuthentication().getName();

      try{
         //获取cookie购物车列表
         List<Cart> cartList = findCartList(request);
         cartList = cartService.addGoodsToCartList(cartList,itemId,num);
         if (username.equals("anonymousUser")){
            //为登录，那么保存到cookie中
            //添加商品到购物车完成后，重新将购物添加到cookie
            CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"utf-8");
         }else {
            //已登录，保存到redis中
            cartService.saveCartToRedis(username,cartList);
         }

         return JdResult.ok();
      }catch (Exception e){
         e.printStackTrace();
         return new JdResult(false,"添加商品到购物车失败",null);
      }
   }
}
