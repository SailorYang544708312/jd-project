package com.jd.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jd.pojo.TbItem;
import com.jd.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
@SuppressWarnings("unchecked")
public class ItemSearchServiceImpl implements ItemSearchService {

   @Autowired
   private SolrTemplate solrTemplate;
   @Autowired
   private RedisTemplate redisTemplate;

   @Override
   public Map<String, Object> search(Map<String, Object> searchMap) {
      Map<String, Object> map = new HashMap<String, Object>();
      //1.高亮的查询 带keywords的查询
      map.putAll(searchList(searchMap));

      //2.根据关键字查询商品分类
      List categoryList = searchCategoryList(searchMap);
      map.put("categoryList",categoryList);

      //3.查询品牌和规格列表(不能始终显示第一个规格，有可能我们会选择第二个分类，那么下面的规格也得跟着变)
      String categoryName = (String) searchMap.get("category");//从查询条件中获取到分类名称
      if (!"".equals(categoryName)){
         //如果有分类名称
         map.putAll(searchBrandAndSpecList(categoryName));
      }else {
         //如果没有分类名称，按照第一个查询
         map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
      }

      return map;
   }

   //高亮显示
   private Map searchList(Map<String, Object> searchMap){
      Map<String, Object> map = new HashMap<String, Object>();
      HighlightQuery query = new SimpleHighlightQuery();
      HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//指定高亮的关键字出现在哪个域
      highlightOptions.setSimplePrefix("<font style='color:red;'>");//高亮的前缀
      highlightOptions.setSimplePostfix("</font>");//高亮的后缀
      query.setHighlightOptions(highlightOptions);//设置高亮选项
      //添加查询条件
      Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
      query.addCriteria(criteria);
      //1.2 按分类查询
      if(!"".equals(searchMap.get("category"))){
         //有分类的条件
         Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
         FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
         query.addFilterQuery(filterQuery);
      }

      //1.3按品牌查询
      if(!"".equals(searchMap.get("brand"))){
         Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
         FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);;
         query.addFilterQuery(filterQuery);
      }

      //1.4 按规格查询
      if(searchMap.get("spec") != null){
         //将规格强行转换成map
         Map<String,String> specMap = (Map)searchMap.get("spec");
         for (String key : specMap.keySet()) {
            //遍历所有的key
            Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
            //System.out.println("item_spec_" + key + "==========" + specMap.get(key));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);;
            query.addFilterQuery(filterQuery);
         }
      }

      HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

      for (HighlightEntry<TbItem> h : page.getHighlighted()) { //循环高亮的入口
         TbItem item = h.getEntity();  //获取到原实体类 (就是我们的item,其中title不带有高亮的)
         if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0){
            item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果集给item对象中的title
         }
      }
      map.put("rows",page.getContent());
      return map;
   }

   //查询分类列表
   private List searchCategoryList(Map<String, Object> searchMap){
      List list = new ArrayList<>();
      Query query = new SimpleQuery();
      //添加查询条件
      Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
      query.addCriteria(criteria);
      //设置分组选项 ,  相当于mysql中的 group by
      GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
      query.setGroupOptions(groupOptions);
      //得到了分组页
      GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
      //根据列得到分组的 结果集
      GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
      //得到分组结果入口
      Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
      //通过结果入口获取分组入口集合（具体的分组数据）
      List<GroupEntry<TbItem>> content = groupEntries.getContent();
      for (GroupEntry<TbItem> entry : content) {
         list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
      }
      return list;
   }

   //查询品牌和规格列表
   private Map searchBrandAndSpecList(String category){
      Map<Object, Object> map = new HashMap<>();
      //第一步:从redis中通过分类名称，抓模板id
      Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
      if (typeId != null){
         //根据模板id查询品牌列表
         List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
         System.out.println("品牌列表:"+brandList);
         map.put("brandList",brandList);

         //根据模板id查询规格列表
         List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
         map.put("specList",specList);
      }
      return map;
   }
}
