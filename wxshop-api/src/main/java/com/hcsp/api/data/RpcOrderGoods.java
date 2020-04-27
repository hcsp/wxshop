package com.hcsp.api.data;

import com.hcsp.api.generate.Order;

import java.io.Serializable;
import java.util.List;

public class RpcOrderGoods implements Serializable {
    private Order order;
    private List<GoodsInfo> goods;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<GoodsInfo> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsInfo> goods) {
        this.goods = goods;
    }
}
