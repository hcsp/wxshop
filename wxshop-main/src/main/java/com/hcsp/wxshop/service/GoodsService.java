package com.hcsp.wxshop.service;

import com.hcsp.api.DataStatus;
import com.hcsp.api.data.PageResponse;
import com.hcsp.api.exceptions.HttpException;
import com.hcsp.wxshop.generate.Goods;
import com.hcsp.wxshop.generate.GoodsExample;
import com.hcsp.wxshop.generate.GoodsMapper;
import com.hcsp.wxshop.generate.Shop;
import com.hcsp.wxshop.generate.ShopMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;

@Service
public class GoodsService {
    private GoodsMapper goodsMapper;
    private ShopMapper shopMapper;

    public GoodsService(GoodsMapper goodsMapper, ShopMapper shopMapper) {
        this.goodsMapper = goodsMapper;
        this.shopMapper = shopMapper;
    }

    public Map<Long, Goods> getIdToGoodsMap(List<Long> goodsId) {
        GoodsExample example = new GoodsExample();
        example.createCriteria().andIdIn(goodsId);
        List<Goods> goods = goodsMapper.selectByExample(example);
        return goods.stream().collect(toMap(Goods::getId, x -> x));
    }

    public Goods createGoods(Goods goods) {
        Shop shop = shopMapper.selectByPrimaryKey(goods.getShopId());

        if (Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            goods.setStatus(DataStatus.OK.getName());
            long id = goodsMapper.insert(goods);
            goods.setId(id);
            return goods;
        } else {
            throw HttpException.forbidden("无权访问！");
        }
    }

    public Goods updateGoods(long id, Goods goods) {
        Shop shop = shopMapper.selectByPrimaryKey(goods.getShopId());

        if (Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            Goods goodsInDb = goodsMapper.selectByPrimaryKey(id);
            if (goodsInDb == null) {
                throw HttpException.notFound("未找到");
            }
            goodsInDb.setName(goods.getName());
            goodsInDb.setDetails(goods.getDetails());
            goodsInDb.setDescription(goods.getDescription());
            goodsInDb.setImgUrl(goods.getImgUrl());
            goodsInDb.setPrice(goods.getPrice());
            goodsInDb.setStock(goods.getStock());
            goodsInDb.setUpdatedAt(new Date());

            goodsMapper.updateByPrimaryKey(goodsInDb);

            return goodsInDb;
        } else {
            throw HttpException.forbidden("无权访问！");
        }
    }

    public Goods deleteGoodsById(Long goodsId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        if (goods == null) {
            throw HttpException.notFound("商品未找到！");
        }
        Shop shop = shopMapper.selectByPrimaryKey(goods.getShopId());

        if (shop != null && Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            goods.setStatus(DataStatus.DELETED.getName());
            goodsMapper.updateByPrimaryKey(goods);
            return goods;
        } else {
            throw HttpException.forbidden("无权访问！");
        }
    }

    public PageResponse<Goods> getGoods(Integer pageNum, Integer pageSize, Long shopId) {
        // 知道有多少个元素
        // 然后才知道有多少页
        // 然后正确地进行分页

        int totalNumber = countGoods(shopId);
        int totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : totalNumber / pageSize + 1;

        GoodsExample page = new GoodsExample();
        page.setLimit(pageSize);
        page.setOffset((pageNum - 1) * pageSize);
        if (shopId != null) {
            page.createCriteria().andShopIdEqualTo(shopId);
        }

        List<Goods> pagedGoods = goodsMapper.selectByExample(page);

        return PageResponse.pagedData(pageNum, pageSize, totalPage, pagedGoods);
    }

    private int countGoods(Long shopId) {
        if (shopId == null) {
            GoodsExample goodsExample = new GoodsExample();
            goodsExample.createCriteria().andStatusEqualTo(DataStatus.OK.getName());
            return (int) goodsMapper.countByExample(goodsExample);
        } else {
            GoodsExample goodsExample = new GoodsExample();
            goodsExample.createCriteria()
                    .andStatusEqualTo(DataStatus.OK.getName())
                    .andShopIdEqualTo(shopId);
            return (int) goodsMapper.countByExample(goodsExample);
        }
    }

    public Goods getGoodsById(long goodsId) {
        GoodsExample okStatus = new GoodsExample();
        okStatus.createCriteria().andIdEqualTo(goodsId)
                .andStatusEqualTo(DataStatus.OK.name());
        List<Goods> goods = goodsMapper.selectByExampleWithBLOBs(okStatus);
        if (goods.isEmpty()) {
            throw HttpException.notFound("商品未找到：" + shopId);
        }
        return goods.get(0);
    }
}
