package com.hcsp.wxshop.service;

import com.hcsp.wxshop.dao.GoodsDao;
import com.hcsp.wxshop.dao.ShopDao;
import com.hcsp.wxshop.generate.Goods;
import com.hcsp.wxshop.generate.Shop;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GoodsService {
    private GoodsDao goodsDao;
    private ShopDao shopDao;

    public GoodsService(GoodsDao goodsDao, ShopDao shopDao) {
        this.goodsDao = goodsDao;
        this.shopDao = shopDao;
    }

    public Goods createGoods(Goods goods) {
        Shop shop = shopDao.findShopById(goods.getShopId());

        if (Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            return goodsDao.insertGoods(goods);
        } else {
            throw new NotAuthorizedForShopException("无权访问！");
        }
    }

    public Goods deleteGoodsById(Long goodsId) {
        Shop shop = shopDao.findShopById(goodsId);

        if (Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            return goodsDao.deleteGoodsById(goodsId);
        } else {
            throw new NotAuthorizedForShopException("无权访问！");
        }
    }

    public static class NotAuthorizedForShopException extends RuntimeException {
        public NotAuthorizedForShopException(String message) {
            super(message);
        }
    }
}
