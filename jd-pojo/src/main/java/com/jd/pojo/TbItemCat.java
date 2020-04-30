package com.jd.pojo;

import java.io.Serializable;

public class TbItemCat implements Serializable {
    private Long id;

    private Long parentId;

    private String name;

    private Long typeId;

    public TbItemCat(Long id, Long parentId, String name, Long typeId) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.typeId = typeId;
    }

    public TbItemCat() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }
}