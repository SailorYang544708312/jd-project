package com.jd.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jd.cart.service.CartService;
import com.jd.mapper.TbItemMapper;
import com.jd.pojo.TbItem;
import com.jd.pojo.TbOrderItem;
import com.jd.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

   @Autowired
   private TbItemMapper itemMapper;

   @Autowired
   private RedisTemplate redisTemplate;

   @Override
   public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
      /*实现添加到购物车的思路*/
      //1.根据商品的sku ID 查询 sku信息
      TbItem item = itemMapper.selectByPrimaryKey(itemId);
      if (item == null){
         throw new RuntimeException("商品不存在");
      }
      if (!item.getStatus().equals("1")){
         throw new RuntimeException("商品状态无效");
      }
      //2.获取到商家id
      String sellerId = item.getSellerId();
      //3.根据商家的id 判断购物车中 是否已经存在该商家的购物车
      Cart cart = searchCartBySellerId(cartList,sellerId);
      //4.如果购物车列表中不存在该商家的购物车
      if (cart == null){
         //4.1不存在 则创建购物车对象
         cart = new Cart();
         cart.setSellerId(sellerId);
         cart.setSellerName(item.getSeller());
         //创建订单明细
         TbOrderItem orderItem = createOrderItem(item,num);

         List<TbOrderItem> orderItemList = new ArrayList();
         orderItemList.add(orderItem);
         //添加到购物车中
         cart.setOrderItemList(orderItemList);
         //4.2将购物车对象添加到购物车列表中
         cartList.add(cart);
      }else {
         //5.如果购物车列表中存在该商家的购物车
         //判断购物车明细列表中是否有该商品
         TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
         if (orderItem == null){
            //5.1如果没有，新增到购物车明细中
            orderItem = createOrderItem(item,num);
            cart.getOrderItemList().add(orderItem);
         }else {
            //5.2如果有，在原来的购物车明细上添加新增的数量,并修改金额
            orderItem.setNum(orderItem.getNum() + num);
            orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));
            //如果后台操作后小于等于0. 则移除
            if (orderItem.getNum() <= 0){
               cart.getOrderItemList().remove(orderItem); //删除购物车明细
            }
            //如果移除后cart的明细数量为0，则cart也移除
            if (cart.getOrderItemList().size() == 0){
               cartList.remove(cart);
            }
         }
      }
      return cartList;
   }

   @Override
   public List<Cart> findCartListFormRedis(String username) {
      System.out.println("从redis中取出购物车");
      List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps("cartList").get(username);
      if (cartList == null){
         cartList = new ArrayList<>();
      }
      return cartList;
   }

   @Override
   public void saveCartToRedis(String username, List<Cart> cartList) {
      System.out.println("像redis中存入购物车数据");
      redisTemplate.boundHashOps("cartList").put(username,cartList);
   }

   /**
    * 根据商品明细id查询
    * @param orderItemList
    * @param itemId
    * @return
    */
   private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
      for (TbOrderItem orderItem : orderItemList) {
         if (orderItem.getItemId().longValue() == itemId.longValue()){
            //要转成原始long类型，不要使用包装的long进行比较
            return orderItem;
         }
      }
      return null;
   }

   /**
    * 创建订单明细
    * @param item
    * @param num
    * @return
    */
   private TbOrderItem createOrderItem(TbItem item, Integer num) {
      if (num <= 0){
         throw new RuntimeException("数量非法");
      }
      TbOrderItem orderItem = new TbOrderItem();
      orderItem.setGoodsId(item.getGoodsId());
      orderItem.setItemId(item.getId());
      orderItem.setNum(num);
      orderItem.setPicPath(item.getImage());
      orderItem.setPrice(item.getPrice());
      orderItem.setSellerId(item.getSellerId());
      orderItem.setTitle(item.getTitle());
      orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
      return orderItem;
   }

   /**
    * 根据商家的id 判断购物车中 是否已经存在该商家的购物车
    * @param cartList
    * @param sellerId
    * @return
    */
   private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
      for (Cart cart : cartList) {
         if (cart.getSellerId().equals(sellerId)){
            return cart;
         }
      }
      return null;
   }
}
