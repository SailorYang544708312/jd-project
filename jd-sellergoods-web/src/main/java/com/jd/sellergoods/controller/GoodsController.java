package com.jd.sellergoods.controller;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.jd.pojo.TbItem;
import com.jd.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.jd.common.pojo.JdResult;
import com.jd.common.pojo.PageResult;
import com.jd.pojo.TbGoods;
import com.jd.sellergoods.service.GoodsService;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

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

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Destination queueSolrDestination;

	@Autowired
	private Destination queueSolrDeleteDestination;

	@Autowired
	private Destination topicItemDestination;

	@Autowired
	private Destination topicItemDeleteDestination;
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
	public JdResult delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			//从solr中也删除
			//itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
			//发送删除的消息到activemq
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					//因为Long实现了序列化接口，那么直接使用createObjectMessage会更加简单,不需要类型转换
					//当然 万物都可以使用TextMessage, 我们只要把它转换成json字符串就可以了
					return session.createObjectMessage(ids);
				}
			});

			//发送到mq准备去删除静态页面
			jmsTemplate.send(topicItemDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});

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

			//将消息发到mq

			//当运营商点击审核通过，就将商品添加到solr中
			if (status.equals("1")){
				//审核通过(审核通过的sku列表)
				List<TbItem> list = goodsService.findItemListByGoodsIdAndStatus(ids, status);
				//调用搜索服务中的添加到solr方法
				if (list != null && list.size() > 0){
					//itemSearchService.importList(list);
					//不在这里业务耦合做添加到solor的操作，而是给activemq发一个消息，然后在search工程监听，然后由search工程添加到solr中
					//生产者，发送消息到activeMQ(使用queue还是topic) --这里使用queue 因为queue消息可以持久化 不消费消息会一直存在(当然使用topic也是可以的)
					//将list集合转换成json字符串
					final String jsonString = JSON.toJSONString(list);
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(jsonString);
						}
					});
				}
				System.out.println("将商品导入到solr成功");

				//通过审核后，通过activemq发送消息; 因为item工程会部署多台服务器
				//如果使用queue模式,那么只有一台服务器能生成静态页面，其他服务器就是个摆设
				for (final Long goodsId : ids) {
					jmsTemplate.send(topicItemDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(goodsId+"");
						}
					});
				}

				//商品审核通过，那么将要生成该商品的详情静态页面
				/*for (Long id : ids) {
					itemPageService.genItemHtml(id);
				}*/

			}
			return JdResult.ok();
		}catch (Exception e){
			e.printStackTrace();
			return new JdResult(false,"修改商品状态失败",null);
		}
	}

	//为了方便测试，先不放在审核出
	/*@RequestMapping("genhtml")
	public JdResult genHtml(Long goodsId){
		JdResult result = itemPageService.genItemHtml(goodsId);
		return result;
	}*/
}
