package com.jd.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jd.common.pojo.JdResult;
import com.jd.common.pojo.PageResult;
import com.jd.pojo.TbBrand;
import com.jd.sellergoods.service.BrandService;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("brand")
public class BrandController {

   @Reference  //注意这里是引用服务！ 不是自动注入
   private BrandService brandService;

   @RequestMapping("show")
   @ResponseBody
   public List<TbBrand> show(){
      List<TbBrand> list = brandService.getAllBrand();
      return list;
   }

   //分页查询
   @RequestMapping("findPage")
   public PageResult findPage(@RequestParam(value = "page",defaultValue = "1")Integer page,
                              @RequestParam(value = "rows",defaultValue = "10")Integer rows){
      return brandService.findPage(page,rows);
   }

   @RequestMapping("add")
   public JdResult add(@RequestBody TbBrand tbBrand){
      try {
         brandService.addBrand(tbBrand);
         return JdResult.ok();
      }catch (Exception e){
         e.printStackTrace();
         return new JdResult(false,"添加失败",null);
      }
   }

   @RequestMapping("findOne")
   public TbBrand findOne(@RequestParam("id")Long id){
      return brandService.findOne(id);
   }

   @RequestMapping("update")
   public JdResult update(@RequestBody TbBrand tbBrand){
      try {
         brandService.updateBrand(tbBrand);
         return JdResult.ok();
      }catch (Exception e){
         e.printStackTrace();
         return new JdResult(false,"修改失败",null);
      }
   }

   @RequestMapping("delete")
   public JdResult delete(@RequestParam("ids")Long[] ids){
      try {
         brandService.deleteBrand(ids);
         return JdResult.ok();
      }catch (Exception e){
         e.printStackTrace();
         return new JdResult(false,"删除失败",null);
      }
   }

   //分页条件查询
   @RequestMapping("search")
   public PageResult search(@RequestParam(value = "page",defaultValue = "1")Integer page,
                            @RequestParam(value = "rows",defaultValue = "10")Integer rows,
                            @RequestBody TbBrand tbBrand){
      return brandService.findPage(tbBrand,page,rows);
   }

   @RequestMapping("selectBrandList")
   public List<Map> selectBrandList(){
      return brandService.selectBrandList();
   }
}
