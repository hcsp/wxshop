package com.hcsp.order.service;

import com.hcsp.api.DataStatus;
import com.hcsp.api.data.OrderInfo;
import com.hcsp.api.generate.Order;
import com.hcsp.api.generate.OrderMapper;
import com.hcsp.api.rpc.OrderRpcService;
import com.hcsp.order.mapper.MyOrderMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.function.BooleanSupplier;

@Service(version = "${wxshop.orderservice.version}")
public class RpcOrderServiceImpl implements OrderRpcService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private MyOrderMapper myOrderMapper;

    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        insertOrder(order);
        myOrderMapper.insertOrders(orderInfo);
        return order;
    }

    private void insertOrder(Order order) {
        order.setStatus(DataStatus.PENDING.getName());

        verify(() -> order.getUserId() == null, "userId不能为空！");
        verify(() -> order.getTotalPrice() == null || order.getTotalPrice().doubleValue() < 0, "totalPrice非法！");
        verify(() -> order.getAddress() == null, "address不能为空！");

        order.setExpressCompany(null);
        order.setExpressId(null);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());

        long id = orderMapper.insert(order);
        order.setId(id);
    }

    private void verify(BooleanSupplier supplier, String message) {
        if (supplier.getAsBoolean()) {
            throw new IllegalArgumentException(message);
        }
    }
}
