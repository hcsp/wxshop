package com.hcsp.wxshop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcsp.wxshop.generate.Shop;

import java.util.List;

public class ShoppingCartData {
    @JsonProperty("shop")
    private Shop shop;
    @JsonProperty("goods")
    private List<GoodsWithNumber> goods;

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<GoodsWithNumber> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsWithNumber> goods) {
        this.goods = goods;
    }
}
