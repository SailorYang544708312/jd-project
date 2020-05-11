package com.jd.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jd.pojo.TbItem;
import com.jd.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

   @Autowired
   private SolrTemplate solrTemplate;

   @Override
   public Map<String, Object> search(Map<String, Object> searchMap) {
      Map<String, Object> map = new HashMap<String, Object>();

      /*Query query = new SimpleQuery();
      //添加查询条件
      Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
      query.addCriteria(criteria);
      ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);*/

      HighlightQuery query = new SimpleHighlightQuery();
      HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//指定高亮的关键字出现在哪个域
      highlightOptions.setSimplePrefix("<font style='color:red;'>");//高亮的前缀
      highlightOptions.setSimplePostfix("</font>");//高亮的后缀
      query.setHighlightOptions(highlightOptions);//设置高亮选项
      //添加查询条件
      Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
      query.addCriteria(criteria);
      HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

      /*List<HighlightEntry<TbItem>> entryList = page.getHighlighted();//这个集合中包含了高亮的信息
      for (HighlightEntry<TbItem> entry : entryList) {
         List<HighlightEntry.Highlight> highlights = entry.getHighlights();//获取高亮的列表
         for (HighlightEntry.Highlight h : highlights) {
            List<String> sns = h.getSnipplets();//每个域都有可能储存多个值
            System.out.println(sns);
         }
      }*/
      for (HighlightEntry<TbItem> h : page.getHighlighted()) { //循环高亮的入口
         TbItem item = h.getEntity();  //获取到原实体类 (就是我们的item,其中title不带有高亮的)
         if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0){
            item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果集给item对象中的title
         }
      }
      //将分页的一页数据添加到map
      map.put("rows",page.getContent());
      return map;
   }
}
