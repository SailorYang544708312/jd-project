package com.jd.item.service;

import com.jd.common.pojo.JdResult;

/**
 * 商品详情接口
 */
public interface ItemPageService {
   /**
    * 根据商品id生成商品详情也静态页面
    * @param goodsId
    * @return
    */
   JdResult genItemHtml(Long goodsId);

   /**
    * 删除静态页面
    * @param goodsIds
    * @return
    */
   JdResult deleteItemHtml(Long[] goodsIds);
}
