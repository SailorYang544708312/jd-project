package com.jd.sellergoods.service;

import com.jd.common.pojo.PageResult;
import com.jd.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {
   //查询所有的品牌
   List<TbBrand> getAllBrand();

   //分页（没有条件）
   PageResult findPage(Integer pageNow,Integer pageSize);

   //添加品牌
   void addBrand(TbBrand tbBrand);

   //查找一个实体
   TbBrand findOne(Long id);

   //修改
   void updateBrand(TbBrand tbBrand);

   //批量删除
   void deleteBrand(Long[] ids);

   //条件查询
   PageResult findPage(TbBrand tbBrand,Integer pageNow,Integer pageSize);

   //品牌下拉框搜索
   List<Map> selectBrandList();
}
