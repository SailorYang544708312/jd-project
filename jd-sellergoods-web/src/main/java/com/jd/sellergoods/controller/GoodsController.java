package com.jd.sellergoods.controller;
import java.util.Arrays;
import java.util.List;

import com.jd.item.service.ItemPageService;
import com.jd.pojo.TbItem;
import com.jd.pojogroup.Goods;
import com.jd.search.service.ItemSearchService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.jd.common.pojo.JdResult;
import com.jd.common.pojo.PageResult;
import com.jd.pojo.TbGoods;
import com.jd.sellergoods.service.GoodsService;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Reference
	private ItemSearchService itemSearchService;

	@Reference
	private ItemPageService itemPageService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加  (我是运营商 我是永远不会去做商品的添加服务的！！！ 请不要解开我 不然 呵呵.)
	 * @param goods
	 * @return
	 */
	/*@RequestMapping("/add")
	public JdResult add(@RequestBody TbGoods goods){
		try {
			goodsService.add(goods);
			return new JdResult(true, "增加成功",null);
		} catch (Exception e) {
			e.printStackTrace();
			return new JdResult(false, "增加失败",null);
		}
	}*/
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public JdResult update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new JdResult(true, "修改成功",null);
		} catch (Exception e) {
			e.printStackTrace();
			return new JdResult(false, "修改失败",null);
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public JdResult delete(Long [] ids){
		try {
			goodsService.delete(ids);
			//从solr中也删除
			itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
			return new JdResult(true, "删除成功" ,null); 
		} catch (Exception e) {
			e.printStackTrace();
			return new JdResult(false, "删除失败" ,null);
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);
	}

	@RequestMapping("updateStatus")
	public JdResult updateStatus(@RequestParam("ids")Long[] ids,
								 @RequestParam("status") String status){
		try {
			goodsService.updateStatus(ids,status);

			//当运营商点击审核通过，就将商品添加到solr中
			if (status.equals("1")){
				//审核通过(审核通过的sku列表)
				List<TbItem> list = goodsService.findItemListByGoodsIdAndStatus(ids, status);
				//调用搜索服务中的添加到solr方法
				if (list != null && list.size() > 0){
					itemSearchService.importList(list);
				}
				System.out.println("将商品导入到solr成功");

				//商品审核通过，那么将要生成该商品的详情静态页面
				for (Long id : ids) {
					itemPageService.genItemHtml(id);
				}
			}
			return JdResult.ok();
		}catch (Exception e){
			e.printStackTrace();
			return new JdResult(false,"修改商品状态失败",null);
		}
	}

	//为了方便测试，先不放在审核出
	@RequestMapping("genhtml")
	public JdResult genHtml(Long goodsId){
		JdResult result = itemPageService.genItemHtml(goodsId);
		return result;
	}
}
