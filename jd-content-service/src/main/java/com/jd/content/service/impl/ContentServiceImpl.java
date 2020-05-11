package com.jd.content.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.mapper.TbContentMapper;
import com.jd.pojo.TbContent;
import com.jd.pojo.TbContentExample;
import com.jd.pojo.TbContentExample.Criteria;
import com.jd.content.service.ContentService;
import com.jd.common.pojo.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private RedisTemplate<String,Object> redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		//新增完广告后，得将redis中的广告删除,不删除就会直接从redis中拿 前端就读不到更新后的数据
		//我们采用的存储模式是hash 结构是  ads = {1:[],2:[],3:[]}    1就是我们设定的首页轮播广告  这样就会只删除首页轮播的广告的redis缓存
		redisTemplate.boundHashOps("ads").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//查询修改前的分类ID(因为 修改时 有可能会修改分类ID)
		TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
		Long categoryId = tbContent.getCategoryId(); //得到修改前的分类ID
		redisTemplate.boundHashOps("ads").delete(categoryId);
		contentMapper.updateByPrimaryKey(content);
		if (categoryId.longValue() != content.getCategoryId()){
			redisTemplate.boundHashOps("ads").delete(content.getCategoryId());
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			redisTemplate.boundHashOps("ads").delete(categoryId);
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {
		//首先从redis中取出数据
		List<TbContent> list = (List<TbContent>)redisTemplate.boundHashOps("ads").get(categoryId);
		//1.取到了直接返回，就不去数据库了
		if (list != null && list.size() > 0){
			System.out.println("直接走redis，不去数据库:"+list);
			return list;
		}
		//2.没取到，去数据库查
		TbContentExample example = new TbContentExample();
		example.setOrderByClause("sort_order desc");//通过降序排列
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		//只查询status状态是1 启用的广告
		criteria.andStatusEqualTo("1");
		list = contentMapper.selectByExample(example);
		//查到后 存入redis 方便下次就不去数据库查了 提升性能
		redisTemplate.boundHashOps("ads").put(categoryId,list);
		System.out.println("走数据库,并且存入到redis中:"+list);
		return list;
	}

}
