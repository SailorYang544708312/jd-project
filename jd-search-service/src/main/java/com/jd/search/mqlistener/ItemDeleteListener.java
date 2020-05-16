package com.jd.search.mqlistener;

import com.alibaba.fastjson.JSON;
import com.jd.pojo.TbItem;
import com.jd.search.service.ItemSearchService;

import javax.annotation.Resource;
import javax.jms.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemDeleteListener implements MessageListener {
   @Resource
   private ItemSearchService itemSearchService;
   @Override
   public void onMessage(Message message) {
      ObjectMessage objectMessage = (ObjectMessage)message;
      try {
         //获取到内容
         Long[] goodsIds = (Long[]) objectMessage.getObject();
         System.out.println("监听到了要到solr中删除的信息:"+goodsIds);
         //执行删除
         itemSearchService.deleteByGoodsIds(Arrays.asList(goodsIds));
         System.out.println("从solr中删除商品成功");
      } catch (JMSException e) {
         e.printStackTrace();
      }
   }
}
