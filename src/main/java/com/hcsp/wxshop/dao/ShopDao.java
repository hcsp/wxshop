package com.hcsp.wxshop.dao;

import com.hcsp.wxshop.generate.Shop;
import com.hcsp.wxshop.generate.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopDao {
    private final ShopMapper shopMapper;

    @Autowired
    public ShopDao(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    public Shop findShopById(Long shopId) {
        return shopMapper.selectByPrimaryKey(shopId);
    }
}
