package com.jd.sellergoods.service;
import java.util.List;
import com.jd.pojo.TbGoods;

import com.jd.common.pojo.PageResult;
import com.jd.pojo.TbItem;
import com.jd.pojogroup.Goods;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(Goods goods);
	
	
	/**
	 * 修改
	 */
	public void update(Goods goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum,int pageSize);

	/**
	 * 批量修改状态
	 * @param ids
	 * @param status
	 */
	void updateStatus(Long[] ids,String status);

	/**
	 * 运营商可以批量审核通过（根据商品的id和状态来查询item表信息）
	 * @param goodsIds
	 * @param status
	 * @return
	 */
	List<TbItem> findItemListByGoodsIdAndStatus(Long[] goodsIds,String status);
}
