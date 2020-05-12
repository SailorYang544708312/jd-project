package com.jd.solr.utils;


import com.alibaba.fastjson.JSON;
import com.jd.mapper.TbItemMapper;
import com.jd.pojo.TbItem;
import com.jd.pojo.TbItemExample;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
   //从数据库里去 查询
   @Resource
   private TbItemMapper itemMapper;
   @Resource
   private SolrTemplate solrTemplate;

   //从数据库中查询商品列表
   public List<TbItem> selectFromMysql(){
      TbItemExample example = new TbItemExample();
      TbItemExample.Criteria criteria = example.createCriteria();
      criteria.andStatusEqualTo("1");//到solr中的商品必须是通过审核的
      List<TbItem> list = itemMapper.selectByExample(example);
      return list;
   }

   //添加到solr
   public void save(){
      List<TbItem> list = selectFromMysql();
      for (TbItem item : list) {
         Map specMap = JSON.parseObject(item.getSpec(), Map.class);
         item.setSpecMap(specMap);//给带注解的字段赋值
      }
      solrTemplate.saveBeans(list);
      solrTemplate.commit();
   }

   public static void main(String[] args) {
      ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");
      SolrUtil solrUtil = (SolrUtil)app.getBean("solrUtil");
      solrUtil.save();
   }


}
