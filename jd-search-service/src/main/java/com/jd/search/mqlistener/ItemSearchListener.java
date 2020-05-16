package com.jd.search.mqlistener;

import com.alibaba.fastjson.JSON;
import com.jd.pojo.TbItem;
import com.jd.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

public class ItemSearchListener implements MessageListener {
   @Resource
   private ItemSearchService itemSearchService;
   @Override
   public void onMessage(Message message) {

      //读取消息
      TextMessage textMessage = (TextMessage)message;
      try {
         //取出消息
         String jsonString = textMessage.getText();
         System.out.println("监听到了添加到solr的消息:"+jsonString);
         //转换为list集合
         List<TbItem> list = JSON.parseArray(jsonString, TbItem.class);
         for (TbItem item : list) {
            Map specMap = JSON.parseObject(item.getSpec());//将spec字段中的json字符串转换成map准备存入solr
            item.setSpecMap(specMap);
         }
         //导入到solr中
         itemSearchService.importList(list);
         System.out.println("导入到solr成功");
      } catch (JMSException e) {
         e.printStackTrace();
      }
   }
}
