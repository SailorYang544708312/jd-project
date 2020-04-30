package com.jd.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jd.common.pojo.JdResult;
import com.jd.common.pojo.PageResult;
import com.jd.pojo.TbGoods;
import com.jd.sellergoods.service.GoodsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public JdResult add(@RequestBody TbGoods goods){
		try {
			goodsService.add(goods);
			return new JdResult(true, "增加成功",null);
		} catch (Exception e) {
			e.printStackTrace();
			return new JdResult(false, "增加失败",null);
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public JdResult update(@RequestBody TbGoods goods){
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
	public TbGoods findOne(Long id){
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
			return new JdResult(true, "删除成功" ,null); 
		} catch (Exception e) {
			e.printStackTrace();
			return new JdResult(false, "删除失败" ,null);
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
	
}
