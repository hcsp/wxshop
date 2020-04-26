package com.hcsp.wxshop.entity;

import com.hcsp.wxshop.generate.Goods;

public class ShoppingCartGoods extends Goods {
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
