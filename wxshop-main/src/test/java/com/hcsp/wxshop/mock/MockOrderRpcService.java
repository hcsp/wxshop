package com.hcsp.wxshop.mock;

import com.hcsp.api.data.OrderInfo;
import com.hcsp.api.generate.Order;
import com.hcsp.api.rpc.OrderRpcService;
import org.apache.dubbo.config.annotation.Service;
import org.mockito.Mock;

@Service(version = "${wxshop.orderservice.version}")
public class MockOrderRpcService implements OrderRpcService {
    @Mock
    public OrderRpcService orderRpcService;

    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        return orderRpcService.createOrder(orderInfo, order);
    }
}
