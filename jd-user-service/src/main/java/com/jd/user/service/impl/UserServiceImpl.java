package com.jd.user.service.impl;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.jd.common.pojo.JdResult;
import com.jd.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.mapper.TbUserMapper;
import com.jd.pojo.TbUser;
import com.jd.pojo.TbUserExample;
import com.jd.pojo.TbUserExample.Criteria;
import com.jd.common.pojo.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.DigestUtils;

import javax.jms.*;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Destination smsDestination;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {
		user.setCreated(new Date());//创建日期
		user.setUpdated(new Date());//更新日期
		user.setSourceType("1");//通过pc注册
		//注册的用户，密码需要通过md5加密后存入到数据库中
		String password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(password);
		userMapper.insert(user);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){
		userMapper.updateByPrimaryKey(user);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		if(user!=null){			
						if(user.getUsername()!=null && user.getUsername().length()>0){
				criteria.andUsernameLike("%"+user.getUsername()+"%");
			}
			if(user.getPassword()!=null && user.getPassword().length()>0){
				criteria.andPasswordLike("%"+user.getPassword()+"%");
			}
			if(user.getPhone()!=null && user.getPhone().length()>0){
				criteria.andPhoneLike("%"+user.getPhone()+"%");
			}
			if(user.getEmail()!=null && user.getEmail().length()>0){
				criteria.andEmailLike("%"+user.getEmail()+"%");
			}
			if(user.getSourceType()!=null && user.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}
			if(user.getNickName()!=null && user.getNickName().length()>0){
				criteria.andNickNameLike("%"+user.getNickName()+"%");
			}
			if(user.getName()!=null && user.getName().length()>0){
				criteria.andNameLike("%"+user.getName()+"%");
			}
			if(user.getStatus()!=null && user.getStatus().length()>0){
				criteria.andStatusLike("%"+user.getStatus()+"%");
			}
			if(user.getHeadPic()!=null && user.getHeadPic().length()>0){
				criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}
			if(user.getQq()!=null && user.getQq().length()>0){
				criteria.andQqLike("%"+user.getQq()+"%");
			}
			if(user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0){
				criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}
			if(user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0){
				criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}
			if(user.getSex()!=null && user.getSex().length()>0){
				criteria.andSexLike("%"+user.getSex()+"%");
			}
	
		}
		
		Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void createSmsCode(final String phone) {
		//生成随机的6为数字
		//final String code = (long)(Math.random()*1000000) + "";
		Random random = new Random();
		final String code = random.nextInt(999999-100000+1) + 100000 + "";
		//存入到redis中(key是手机号，value是验证码)
		redisTemplate.boundValueOps(phone).set(code);
		System.out.println("生成验证码是（并且已经到redis中）：" + code);
		//设置在redis中存在1个小时(实际开发中，短信都是在redis中存在60秒)
		redisTemplate.boundValueOps(phone).expire(1*60*60, TimeUnit.SECONDS);
		//发送到activeMQ
		jmsTemplate.send(smsDestination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("mobile", phone);//手机号
				mapMessage.setString("code", code);//验证码(验证码如果这么玩会导致阿里大于拿不到)
				mapMessage.setString("sign", "咧嘴小怪兽");//签名
				mapMessage.setString("templateName", "SMS_183796269");//模板
				/*Map<String, String> map = new HashMap<>();
				map.put("code", code);
				mapMessage.setString("code", JSON.toJSONString(map));*/
				return mapMessage;
			}
		});

	}

	@Override
	public JdResult checkSmsCode(String phone, String code) {
		//从reids中取出验证码
		String sysCode = (String)redisTemplate.boundValueOps(phone).get();
		if(sysCode == null){
			//要么就没有发送验证码，要么验证码过期
			return new JdResult(false, "验证码过期", null);
		}
		if(!sysCode.equals(code)){
			return new JdResult(false, "验证码输入错误", null);
		}
		return JdResult.ok();
	}

}
