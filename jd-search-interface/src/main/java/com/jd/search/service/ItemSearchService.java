package com.jd.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
   /**
    * 搜索
    * @param searchMap
    * @return
    */
   Map<String,Object> search(Map<String,Object> searchMap);

   /**
    * 导入数据到solr中
    * @param list
    */
   void importList(List list);

   /**
    * 从solr中删除数据
    * @param goodsIdList
    */
   void deleteByGoodsIds(List goodsIdList);
}
