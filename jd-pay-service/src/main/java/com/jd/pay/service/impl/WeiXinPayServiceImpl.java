package com.jd.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.jd.common.utils.HttpClient;
import com.jd.pay.service.WeiXinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {

   @Value("${appid}")
   private String appid;
   @Value("${mch_id}")
   private String mch_id;
   @Value("${KEY}")
   private String KEY;

   @Override
   public Map<String, String> createNative(String out_trade_no, String total_fee) {
      //1.将统一下单的请求参数封装到map中
      Map<String, String> param = new HashMap<>();
      param.put("appid",appid); //公众账号ID
      param.put("mch_id",mch_id); //商户号
      param.put("nonce_str", WXPayUtil.generateNonceStr()); //随机字符串(为了安全)
      //param.put("sign",""); //签名
      param.put("body","京东购物商城"); //商品描述
      param.put("out_trade_no",out_trade_no); //商户订单号
      param.put("total_fee",total_fee); //标价金额
      param.put("spbill_create_ip","127.0.0.1"); //终端IP
      param.put("notify_url","http://www.finish.com"); //通知地址(支付成功后跳转的地址，在微信模式二中这个条件不是必须的 但是是必须字段！可随便写 满足规格即可)
      param.put("trade_type","NATIVE"); //交易类型

      //2.转成xml
      try {
         String xmlParam = WXPayUtil.generateSignedXml(param, KEY);
         System.out.println("将参数封装到map中,生成的xml是:"+xmlParam);

         //3.使用POST方式 向微信统一下单的接口发送封装的xml(https://api.mch.weixin.qq.com/pay/unifiedorder)
         String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
         HttpClient client = new HttpClient(url);
         client.setHttps(true); //使用https协议
         client.setXmlParam(xmlParam); //发送上面第2步骤封装好的xml文件
         client.post(); //使用POST方式发送

         //4.发送后 获取到微信后台返回过来的结果
         String result = client.getContent(); //返回的结果还是一个xml
         System.out.println("统一下单后微信支付后台返回的结果:"+result);
         //将返回的xml转换成map
         //注:返回的xml结果中有很多数据，我们只取出我们需要的数据，不要的我们就不取
         Map<String, String> resultMap = WXPayUtil.xmlToMap(result);

         Map<String, String> map = new HashMap<>();
         map.put("code_url",resultMap.get("code_url")); //支付的地址
         map.put("out_trade_no",out_trade_no); //订单号
         map.put("total_fee",total_fee); //支付金额

         return map;
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   @Override
   public Map<String, String> queryPayStatus(String out_trade_no) {
      //1.将请求参数封装到map
      Map<String, String> param = new HashMap<>();
      param.put("appid", appid);	//公众账号ID
      param.put("mch_id", mch_id);//商户id
      param.put("out_trade_no", out_trade_no);//商户订单号
      param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串

      try {
         //2.将map转化成xml
         String xmlParam = WXPayUtil.generateSignedXml(param, KEY);
         HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
         client.setHttps(true);
         client.setXmlParam(xmlParam);
         //3.使用httclient发送xml到（订单查询接口https://api.mch.weixin.qq.com/pay/orderquery）
         client.post();

         //4.拿到返回结果
         String result = client.getContent();
         //将xml转化成map对象
         Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
         System.out.println("查询订单的情况：" + resultMap);
         return resultMap;
      } catch (Exception e) {
         e.printStackTrace();
      }

      return null;
   }
}
