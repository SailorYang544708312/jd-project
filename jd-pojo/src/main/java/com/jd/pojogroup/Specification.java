package com.jd.pojogroup;

import com.jd.pojo.TbSpecification;
import com.jd.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

public class Specification implements Serializable {
   private TbSpecification specification;  //规格
   private List<TbSpecificationOption> specificationOptionList; //规格选项

   public TbSpecification getSpecification() {
      return specification;
   }

   public void setSpecification(TbSpecification specification) {
      this.specification = specification;
   }

   public List<TbSpecificationOption> getSpecificationOptionList() {
      return specificationOptionList;
   }

   public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
      this.specificationOptionList = specificationOptionList;
   }
}
