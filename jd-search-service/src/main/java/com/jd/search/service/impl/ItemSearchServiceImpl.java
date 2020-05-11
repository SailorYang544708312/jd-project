package com.jd.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jd.pojo.TbItem;
import com.jd.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;


import java.util.HashMap;
import java.util.Map;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

   @Autowired
   private SolrTemplate solrTemplate;

   @Override
   public Map<String, Object> search(Map<String, Object> searchMap) {
      Map<String, Object> map = new HashMap<String, Object>();

      Query query = new SimpleQuery();
      //添加查询条件
      Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
      query.addCriteria(criteria);
      ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
      //将分页的一页数据添加到map
      map.put("rows",page.getContent());
      return map;
   }
}
