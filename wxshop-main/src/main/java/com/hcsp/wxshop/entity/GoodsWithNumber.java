package com.hcsp.wxshop.entity;

import com.hcsp.wxshop.generate.Goods;

public class GoodsWithNumber extends Goods {

    public GoodsWithNumber() {
    }

    public GoodsWithNumber(Goods goods) {
        this.setId(goods.getId());
        this.setShopId(goods.getShopId());
        this.setName(goods.getName());
        this.setDescription(goods.getDescription());
        this.setImgUrl(goods.getImgUrl());
        this.setPrice(goods.getPrice());
        this.setStock(goods.getStock());
        this.setStatus(goods.getStatus());
        this.setCreatedAt(goods.getCreatedAt());
        this.setUpdatedAt(goods.getUpdatedAt());
        this.setDetails(goods.getDetails());
    }

    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
