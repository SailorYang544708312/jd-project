package com.jd.cart.service;

import com.jd.pojogroup.Cart;

import java.util.List;

public interface CartService {

   /**
    * 将商品添加到购物车
    */
   List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

   /**
    * 从redis中查询购物车(购物车存入到redis采用hash  (cart) key是username value是购物车)
    */
   List<Cart> findCartListFormRedis(String username);

   /**
    * 将购物车存入到redis中
    * @param username
    * @param cartList
    */
   void saveCartToRedis(String username,List<Cart> cartList);
}
