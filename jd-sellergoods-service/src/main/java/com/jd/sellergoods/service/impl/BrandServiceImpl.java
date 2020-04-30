package com.jd.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.common.pojo.PageResult;
import com.jd.mapper.TbBrandMapper;
import com.jd.pojo.TbBrand;
import com.jd.pojo.TbBrandExample;
import com.jd.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service   //注意这个注解的包别导入错了 此时用的是dubbo的service
public class BrandServiceImpl implements BrandService {

   @Autowired
   private TbBrandMapper tbBrandMapper;

   @Override
   public List<TbBrand> getAllBrand() {
      TbBrandExample example = new TbBrandExample();
      List<TbBrand> list = tbBrandMapper.selectByExample(example);
      return list;
   }

   @Override
   public PageResult findPage(Integer pageNow, Integer pageSize) {
      //通过pageHepler开始分页
      PageHelper.startPage(pageNow,pageSize);
      Page<TbBrand> page = (Page<TbBrand>)tbBrandMapper.selectByExample(null);
      return new PageResult(page.getTotal(),page.getResult());
   }

   @Override
   public void addBrand(TbBrand tbBrand) {
      tbBrandMapper.insertSelective(tbBrand);
   }

   @Override
   public TbBrand findOne(Long id) {
      return tbBrandMapper.selectByPrimaryKey(id);
   }

   @Override
   public void updateBrand(TbBrand tbBrand) {
      tbBrandMapper.updateByPrimaryKeySelective(tbBrand);
   }

   @Override
   public void deleteBrand(Long[] ids) {
      for (Long id : ids) {
         tbBrandMapper.deleteByPrimaryKey(id);
      }
   }

   @Override
   public PageResult findPage(TbBrand tbBrand, Integer pageNow, Integer pageSize) {
      //开始分页
      PageHelper.startPage(pageNow,pageSize);
      //执行查询
      TbBrandExample example = new TbBrandExample();
      TbBrandExample.Criteria criteria = example.createCriteria();
      if (tbBrand != null){
         if (tbBrand.getName() != null && !"".equals(tbBrand.getName())){
            criteria.andNameLike("%"+tbBrand.getName()+"%");
         }
         if (tbBrand.getFirstChar() != null && !"".equals(tbBrand.getFirstChar())){
            criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
         }
      }
      Page<TbBrand> page = (Page<TbBrand>)tbBrandMapper.selectByExample(example);
      return new PageResult(page.getTotal(),page.getResult());
   }
}
