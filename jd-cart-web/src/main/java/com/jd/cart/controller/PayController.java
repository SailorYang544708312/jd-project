package com.jd.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jd.common.pojo.JdResult;
import com.jd.common.utils.IdWorker;
import com.jd.order.service.OrderService;
import com.jd.pay.service.WeiXinPayService;
import com.jd.pojo.TbPayLog;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("pay")
public class PayController {

   @Reference
   private WeiXinPayService weiXinPayService;

   @Reference
   private OrderService orderService;


   @RequestMapping("createNative")
   public Map<String,String> createNative(){
      //IdWorker idWorker = new IdWorker();
      //实际的情况应该是:订单id应该是从后台拿的数据,应该支付的金额也应该是购物车中的商品的总金额(而不是死数据)
      //return weiXinPayService.createNative(idWorker.nextId()+"","1");
      //生成订单后，将redis中的数据清空,然后将数据添加到order和orderItem表中
      //添加到order表中生成订单的时候，如果买了多个商家的商品，会有多个订单号，但是此处支付的时候只有一个订单号
      //支付的订单号，就不应该是tb_order中的订单号

      //此处的支付订单是:tb_pay_log中的。这个支付的订单号，用来记录支付的记录
      //从Security中取出当前用户名
      String userId = SecurityContextHolder.getContext().getAuthentication().getName();
      //从redis中取出支付日志
      TbPayLog payLog = orderService.searchPayLogFromRedis(userId);
      System.out.println(payLog);
      if (payLog != null){
         return weiXinPayService.createNative(payLog.getOutTradeNo()+"",payLog.getTotalFee()+"");
      }else {
         return new HashMap<String ,String>();
      }

   }

   @RequestMapping("queryPayStatus")
   public JdResult queryPayStatus(String out_trade_no){
      int x = 0;
      while (true){
         Map<String, String> map = weiXinPayService.queryPayStatus(out_trade_no);
         if (map == null){
            return new JdResult(false,"支付出错",null);
         }

         if (map.get("trade_state").equals("SUCCESS")){
            //交易成功
            //修改订单状态
            orderService.updateOrderStatus(out_trade_no,map.get("transaction_id"));
            return JdResult.ok();
         }
         //如果直接返回，就没时间支付
         try {
            Thread.sleep(3000); //3秒延时
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         //为了防止无休止的循环，定义一个变量x用来做次数的限制,设置时间为5分钟 5*60=300秒
         x++;
         if (x >= 100){
            return new JdResult(false,"二维码超时",null);
         }
      }
   }
}
