package com.jd.pojo;

import java.io.Serializable;

public class TbGoodsDesc implements Serializable {
    private Long goodsId;

    private String specificationItems;

    private String customAttributeItems;

    private String itemImages;

    private String packageList;

    private String saleService;

    private String introduction;

    public TbGoodsDesc(Long goodsId, String specificationItems, String customAttributeItems, String itemImages, String packageList, String saleService) {
        this.goodsId = goodsId;
        this.specificationItems = specificationItems;
        this.customAttributeItems = customAttributeItems;
        this.itemImages = itemImages;
        this.packageList = packageList;
        this.saleService = saleService;
    }

    public TbGoodsDesc(Long goodsId, String specificationItems, String customAttributeItems, String itemImages, String packageList, String saleService, String introduction) {
        this.goodsId = goodsId;
        this.specificationItems = specificationItems;
        this.customAttributeItems = customAttributeItems;
        this.itemImages = itemImages;
        this.packageList = packageList;
        this.saleService = saleService;
        this.introduction = introduction;
    }

    public TbGoodsDesc() {
        super();
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getSpecificationItems() {
        return specificationItems;
    }

    public void setSpecificationItems(String specificationItems) {
        this.specificationItems = specificationItems == null ? null : specificationItems.trim();
    }

    public String getCustomAttributeItems() {
        return customAttributeItems;
    }

    public void setCustomAttributeItems(String customAttributeItems) {
        this.customAttributeItems = customAttributeItems == null ? null : customAttributeItems.trim();
    }

    public String getItemImages() {
        return itemImages;
    }

    public void setItemImages(String itemImages) {
        this.itemImages = itemImages == null ? null : itemImages.trim();
    }

    public String getPackageList() {
        return packageList;
    }

    public void setPackageList(String packageList) {
        this.packageList = packageList == null ? null : packageList.trim();
    }

    public String getSaleService() {
        return saleService;
    }

    public void setSaleService(String saleService) {
        this.saleService = saleService == null ? null : saleService.trim();
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction == null ? null : introduction.trim();
    }
}