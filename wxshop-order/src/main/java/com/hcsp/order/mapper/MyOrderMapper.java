package com.hcsp.order.mapper;

import com.hcsp.api.data.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyOrderMapper {
    void insertOrders(OrderInfo orderInfo);
}
