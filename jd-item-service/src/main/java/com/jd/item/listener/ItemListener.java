package com.jd.item.listener;

import com.jd.item.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ItemListener implements MessageListener {

   @Autowired
   private ItemPageService itemPageService;

   @Override
   public void onMessage(Message message) {
      TextMessage textMessage = (TextMessage)message;
      try {
         //将商品编号quchu
         String text = textMessage.getText();
         System.out.println("获取到要生成静态页面的商品编号"+text);
         //生成静态页面
         itemPageService.genItemHtml(Long.parseLong(text));
      } catch (JMSException e) {
         e.printStackTrace();
      }
   }
}
