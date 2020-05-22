package com.jd.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jd.common.utils.IdWorker;
import com.jd.mapper.TbOrderItemMapper;
import com.jd.mapper.TbPayLogMapper;
import com.jd.order.service.OrderService;
import com.jd.pojo.TbOrderItem;
import com.jd.pojo.TbPayLog;
import com.jd.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.mapper.TbOrderMapper;
import com.jd.pojo.TbOrder;
import com.jd.pojo.TbOrderExample;
import com.jd.pojo.TbOrderExample.Criteria;
import com.jd.common.pojo.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbPayLogMapper payLogMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		//orderMapper.insert(order);
		//获取到购物车中的数据
		List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps("cartList").get(order.getUserId());
		List<String> orderIdList = new ArrayList<>();//订单id列表
		double total_money = 0; //总金额(元)
		//遍历集合取出每一个cart
		for (Cart cart : cartList) {
			TbOrder tbOrder = new TbOrder();
			//通过雪花算法生成订单号
			long orderId = idWorker.nextId();
			tbOrder.setOrderId(orderId); //订单号
			tbOrder.setUserId(order.getUserId()); //用户名
			tbOrder.setPaymentType(order.getPaymentType()); //支付类型
			tbOrder.setStatus("1"); //状态：未付款
			tbOrder.setCreateTime(new Date()); //创建订单时间
			tbOrder.setUpdateTime(new Date()); //修改时间
			tbOrder.setSourceType(order.getSourceType()); //订单来源
			tbOrder.setSellerId(cart.getSellerId()); //商家id
			tbOrder.setReceiverAreaName(order.getReceiverAreaName()); //地址
			tbOrder.setReceiverMobile(order.getReceiverMobile()); //收件人手机号
			tbOrder.setReceiver(order.getReceiver()); //收件人

			//循环orderItemList
			double money = 0;
			for (TbOrderItem orderItem : cart.getOrderItemList()) {
				orderItem.setId(idWorker.nextId()); //通过雪花算法生成订单详情id
				orderItem.setOrderId(orderId); //订单id
				orderItem.setSellerId(cart.getSellerId()); //商家id
				money += orderItem.getTotalFee().doubleValue(); //金额累加
				orderItemMapper.insertSelective(orderItem);
			}
			tbOrder.setPayment(new BigDecimal(money)); //总金额
			orderMapper.insertSelective(tbOrder);

			//将每一次生成的订单号加入到订单id列表中
			orderIdList.add(orderId+"");
			total_money += money; //累加得到总金额
		}

		if ("1".equals(order.getPaymentType())){
			//货到付款不走支付日志
			TbPayLog payLog = new TbPayLog();
			payLog.setOutTradeNo(idWorker.nextId()+""); //支付订单号

			String ids = orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
			payLog.setOrderList(ids); //一个支付订单号，可以对应多个订单
			payLog.setCreateTime(new Date()); //订单创建时间
			payLog.setPayType("1"); //表示是微信支付  2是货到付款
			payLog.setTradeState("0"); //目前是未支付的
			payLog.setUserId(order.getUserId()); //用户名
			payLog.setTotalFee((long)(total_money * 100)); //总金额(分)

			payLogMapper.insertSelective(payLog); //插入到支付日志表中

			//将支付日志放入到redis
			redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);
		}

		//将redis中购物车的信息生成订单，存入到了数据库中，那么redis中没有必要在继续存在了
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 从redis中获取支付日志
	 * @param userId
	 * @return
	 */
	@Override
	public TbPayLog searchPayLogFromRedis(String userId) {
		return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
	}

	/**
	 * 修改支付日志状态
	 * @param out_trade_no		支付订单号
	 * @param transaction_id	微信交易流水号
	 */
	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		payLog.setPayTime(new Date()); //支付完成的时间
		payLog.setTradeState("1"); //已经支付
		payLog.setTransactionId(transaction_id); //微信交易的流水号
		payLogMapper.updateByPrimaryKeySelective(payLog); //修改

		//2.修改订单中的状态
		String orderList = payLog.getOrderList(); //获取到订单号列表
		String[] orderIds = orderList.split(",");
		for (String orderId : orderIds) {
			//查询
			TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
			if (order != null){
				order.setStatus("2"); //订单状态改为已付款
				orderMapper.updateByPrimaryKeySelective(order);
			}
		}
		//支付日志在redis中主要是传递订单号，再就是这里的修改 之后就没用了
		redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
	}

}
