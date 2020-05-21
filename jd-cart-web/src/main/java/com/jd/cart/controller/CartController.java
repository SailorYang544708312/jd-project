package com.jd.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.jd.cart.service.CartService;
import com.jd.common.pojo.JdResult;
import com.jd.common.utils.CookieUtil;
import com.jd.pojogroup.Cart;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
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
   public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response){
      //获取到当前登录人账号
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      System.out.println(username);

      //从cookie中获取到购物车
      String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
      //判断购物车是否存在
      if(cartListString == null || cartListString.equals("")){
         //不存在
         cartListString = "[]";
      }
      //存在,要么同上上面的代码创建了一个空的，要么是本身已经在cookie中有
      List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);

      if(username.equals("anonymousUser")){
         //未登录
         return cartList_cookie;
      } else {
         //已经登录
         List<Cart> cartList_redis = cartService.findCartListFormRedis(username);//从reids中取出数据
         //已经登录了，那么考虑将cookie中的购物车和reids中的购物车合并
         if( cartList_cookie.size() > 0){
            //本地cookie存在购物车
            //合并购物车
            cartList_redis = cartService.mergeCartList(cartList_redis, cartList_cookie);
            //合并完了后，要清除本地cookie的数据
            CookieUtil.deleteCookie(request, response, "cartList");
            //合并完成后，还得将合并后的数据存入到reids中
            cartService.saveCartToRedis(username, cartList_redis);
         }
         return cartList_redis;
      }


   }


   /**
    * 添加商品到购物车
    * CrossOrigin(origins = "http://localhost:9632",allowCredentials = "true")的allowCredentials = "true" 可以省略 在注解里默认就是 true
    */
   @RequestMapping("addGoodsToCartList")
   @CrossOrigin(origins = "http://localhost:9632")
   public JdResult addGoodsToCartList(Long itemId,Integer num,HttpServletRequest request,HttpServletResponse response){
      //添加头信息,让我们的请求支持跨域请求(js的跨域请求)
      //头信息的key是Access-Control-Allow-Origin(表示请求跨域) values是具体的跨域地址(可以写成*,表示允许所有的url都跨域)
      //response.setHeader("Access-Control-Allow-Origin","http://localhost:9632");
      //如果涉及到cookie的操作,还得加上另一个头信息,如果value为true就表示允许跨域做cookie的操作(如果上面跨域允许的url是* 那么就无法实现cookie的操作)
      //response.setHeader("Access-Control-Allow-Credentials","true");

      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      try {
         //获取到cookie购物车列表
         List<Cart> cartList = findCartList(request,response);
         //添加商品到购物车
         cartList = cartService.addGoodsToCartList(cartList, itemId, num);
         if(username.equals("anonymousUser")){
            //未登录，那么保存到cookie中
            //添加商品到购物车完成后，重新将购物添加到cookie
            CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList),3600*24,"UTF-8");
         } else {
            //已经登录，保存redis中
            cartService.saveCartToRedis(username, cartList);
         }
         return JdResult.ok();
      } catch (Exception e) {
         e.printStackTrace();
         return new JdResult(false, "添加商品到购物车失败", null);
      }
   }

}
