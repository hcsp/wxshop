package com.hcsp.api.data;

import java.io.Serializable;
import java.util.List;

public class OrderInfo implements Serializable {
    private long orderId;
    private String address;
    private List<GoodsInfo> goods;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public List<GoodsInfo> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsInfo> goods) {
        this.goods = goods;
    }
}
