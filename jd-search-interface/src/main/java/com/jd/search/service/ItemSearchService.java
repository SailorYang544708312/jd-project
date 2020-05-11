package com.jd.search.service;

import java.util.Map;

public interface ItemSearchService {
   /**
    * 搜索
    * @param searchMap
    * @return
    */
   Map<String,Object> search(Map<String,Object> searchMap);
}
