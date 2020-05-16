package com.jd.item.listener;

import com.jd.item.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

public class ItemDeleteListener implements MessageListener {
   @Autowired
   private ItemPageService itemPageService;
   @Override
   public void onMessage(Message message) {
      ObjectMessage objectMessage = (ObjectMessage)message;
      try {
         //取出消息
         Long [] goodsIds = (Long[]) objectMessage.getObject();
         System.out.println("取出要删除的静态页面的商品ids:"+goodsIds);
         //执行删除
         itemPageService.deleteItemHtml(goodsIds);
      } catch (JMSException e) {
         e.printStackTrace();
      }
   }
}
