package com.jd.pay.service;

import java.util.Map;

public interface WeiXinPayService {

   /**
    * 生成微信支付的二维码
    * @param out_trade_no 商户订单号
    * @param total_fee    标价金额
    * @return
    */
   Map<String,String> createNative(String out_trade_no,String total_fee);


   Map<String,String> queryPayStatus(String out_trade_no);
}
