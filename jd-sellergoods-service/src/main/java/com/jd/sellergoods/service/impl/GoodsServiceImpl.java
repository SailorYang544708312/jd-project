package com.jd.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.mapper.*;
import com.jd.pojo.*;
import com.jd.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.pojo.TbGoodsExample.Criteria;
import com.jd.sellergoods.service.GoodsService;
import com.jd.common.pojo.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemMapper itemMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//商品添加 默认状态是 未审核
		goods.getGoods().setAuditStatus("0");
		goodsMapper.insert(goods.getGoods()); //添加spu

		//获取商品详情的id
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());

		//添加商品信息到sku
		saveItemList(goods);
	}

	private void saveItemList(Goods goods){
		//判断是否启动了规格
		if ("1".equals(goods.getGoods().getIsEnableSpec())){
			//启用规格
			//添加sku列表
			for (TbItem item:goods.getItemList()) {
				//标题(本来的商品名称+规格列表)
				String title = goods.getGoods().getGoodsName();
				Map<String,Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title += " "+specMap.get(key);
				}
				item.setTitle(title);
				setItemValues(goods,item);
				//添加到Tbitem表中
				itemMapper.insertSelective(item);
			}
		}else {
			//不启用规格
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());
			item.setPrice(goods.getGoods().getPrice());
			item.setStatus("0");//新建的商品都是未审核
			item.setNum(9999);//默认库存
			item.setIsDefault("1");//默认就一个商品
			item.setSpec("{}");//规格是空（因为没有启用）
			setItemValues(goods,item);
			itemMapper.insert(item);
		}
	}
	//抽取一个方法
	private void setItemValues(Goods goods, TbItem item) {
		item.setGoodsId(goods.getGoods().getId());//商品SPU编号
		item.setSellerId(goods.getGoods().getSellerId());//商家id
		item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号(放的三级分类编号)
		item.setCreateTime(new Date());//创建时间
		item.setUpdateTime(new Date());//更新时间

		//品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());

		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());

		//商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());

		//图片(取spu的第一张图片)
		String itemImages = goods.getGoodsDesc().getItemImages();
		List<Map> imageList = JSON.parseArray(itemImages, Map.class);
		if (imageList.size()>0){
			//取第一张
			item.setImage((String)imageList.get(0).get("url"));
		}

	}

	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//goodsMapper.updateByPrimaryKey(goods);
		//修改后的商品需要从新通过运营商审核
		goods.getGoods().setAuditStatus("0");
		goodsMapper.updateByPrimaryKey(goods.getGoods());//修改商品表

		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());//修改商品详情表

		//修改sku思路，先把之前的sku全部删除(条件商品的id)，然后加上新的sku表
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		//添加新的sku列表到数据库
		saveItemList(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		//1.查询TbGoods
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		//2.查询TbGoodsDesc
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(goodsDesc);
		//3.查询sku列表
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);//添加商品id作为条件
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goods.setItemList(itemList);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//goodsMapper.deleteByPrimaryKey(id);
			//实现逻辑删除
			TbGoods goods = new TbGoods();
			goods.setId(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKeySelective(goods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();//为null就是 正常商品 为1表示是逻辑删除，我们查询的商品一定是没有删除的
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//为了防止查询到其他商家的商品，所以这里采取精确查询
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods goods = new TbGoods();
			goods.setId(id);
			goods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKeySelective(goods);
		}
	}

}
