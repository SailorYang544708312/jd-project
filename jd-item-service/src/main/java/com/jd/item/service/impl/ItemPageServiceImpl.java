package com.jd.item.service.impl;

import com.jd.common.pojo.JdResult;
import com.jd.item.service.ItemPageService;
import com.jd.mapper.TbGoodsDescMapper;
import com.jd.mapper.TbGoodsMapper;
import com.jd.mapper.TbItemCatMapper;
import com.jd.mapper.TbItemMapper;
import com.jd.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

   @Autowired
   private FreeMarkerConfig freeMarkerConfig;
   @Autowired
   private TbGoodsMapper goodsMapper;
   @Autowired
   private TbGoodsDescMapper goodsDescMapper;
   @Autowired
   private TbItemCatMapper itemCatMapper;
   @Autowired
   private TbItemMapper itemMapper;

   //自动注入属性和值
   @Value("${ITEM_PAGE_DIR}")
   private String ITEM_PAGE_DIR;
   @Value("${ITEM_PAGE_SUFFIX}")
   private String ITEM_PAGE_SUFFIX;
   @Override
   public JdResult genItemHtml(Long goodsId) {
      try {
         //创建方式改为通过spring管理创建
         Configuration con = freeMarkerConfig.getConfiguration();
         Template template = con.getTemplate("item.ftl");
         //创建数据模型对象
         Map<String, Object> dataModel = new HashMap<>();
         //1.加载商品表(tb_goods)
         TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
         dataModel.put("goods",goods);
         //2.加载商品扩展表数据(tb_goodsDesc)
         TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
         dataModel.put("goodsDesc",goodsDesc);
         //3.查询tbGoods表能得到1，2，3级分类id，可以根据分类id到tb_goods_cat表中查询到对应的id 从而得到分类名称
         String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
         String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
         String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
         dataModel.put("itemCat1",itemCat1);
         dataModel.put("itemCat2",itemCat2);
         dataModel.put("itemCat3",itemCat3);

         //4.根据商品id，查询sku列表
         TbItemExample example = new TbItemExample();
         //为了后期打开页面就是默认推荐的商品
         example.setOrderByClause("is_default desc"); //按照降序排序，那么默认推荐的sku排在第一个
         TbItemExample.Criteria criteria = example.createCriteria();
         criteria.andGoodsIdEqualTo(goodsId);//添加商品id作为条件查询
         List<TbItem> itemList = itemMapper.selectByExample(example);
         dataModel.put("itemList",itemList);

         //生成的静态页面的名字就是：商品id.html
         Writer out = new FileWriter(new File(ITEM_PAGE_DIR + goodsId + ITEM_PAGE_SUFFIX));
         //System.out.println("=============================================="+ITEM_PAGE_DIR + goodsId + ITEM_PAGE_SUFFIX+"==============================================");
         template.process(dataModel,out);
         out.close();
         return JdResult.ok();
      }catch (Exception e){
         e.printStackTrace();
         return new JdResult(false,"生成静态页面失败",null);
      }
   }

   @Override
   public JdResult deleteItemHtml(Long[] goodsIds) {
      try {
         for (Long goodsId : goodsIds) {
            new File(ITEM_PAGE_DIR+goodsId+ITEM_PAGE_SUFFIX).delete();
         }
         return JdResult.ok();
      }catch (Exception e){
         e.printStackTrace();
         return new JdResult(false,"删除静态页面失败",null);
      }
   }
}
