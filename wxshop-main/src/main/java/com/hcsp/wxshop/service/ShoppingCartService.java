package com.hcsp.wxshop.service;

import com.hcsp.api.DataStatus;
import com.hcsp.wxshop.controller.ShoppingCartController;
import com.hcsp.wxshop.dao.ShoppingCartQueryMapper;
import com.hcsp.wxshop.entity.GoodsWithNumber;
import com.hcsp.wxshop.entity.HttpException;
import com.hcsp.wxshop.entity.PageResponse;
import com.hcsp.wxshop.entity.ShoppingCartData;
import com.hcsp.wxshop.generate.Goods;
import com.hcsp.wxshop.generate.GoodsMapper;
import com.hcsp.wxshop.generate.ShoppingCart;
import com.hcsp.wxshop.generate.ShoppingCartMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class ShoppingCartService {
    private static Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);
    private ShoppingCartQueryMapper shoppingCartQueryMapper;
    private GoodsMapper goodsMapper;
    private SqlSessionFactory sqlSessionFactory;
    private GoodsService goodsService;

    @Autowired
    public ShoppingCartService(ShoppingCartQueryMapper shoppingCartQueryMapper,
                               GoodsMapper goodsMapper,
                               SqlSessionFactory sqlSessionFactory,
                               GoodsService goodsService) {
        this.shoppingCartQueryMapper = shoppingCartQueryMapper;
        this.goodsMapper = goodsMapper;
        this.sqlSessionFactory = sqlSessionFactory;
        this.goodsService = goodsService;
    }

    public PageResponse<ShoppingCartData> getShoppingCartOfUser(Long userId,
                                                                int pageNum,
                                                                int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        int totalNum = shoppingCartQueryMapper.countHowManyShopsInUserShoppingCart(userId);
        List<ShoppingCartData> pagedData = shoppingCartQueryMapper.selectShoppingCartDataByUserId(userId, pageSize, offset)
                .stream()
                .collect(groupingBy(shoppingCartData -> shoppingCartData.getShop().getId()))
                .values()
                .stream()
                .map(this::merge)
                .collect(toList());

        int totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        return PageResponse.pagedData(pageNum, pageSize, totalPage, pagedData);
    }

    private ShoppingCartData merge(List<ShoppingCartData> goodsOfSameShop) {
        ShoppingCartData result = new ShoppingCartData();
        result.setShop(goodsOfSameShop.get(0).getShop());
        List<GoodsWithNumber> goods = goodsOfSameShop.stream()
                .map(ShoppingCartData::getGoods)
                .flatMap(List::stream)
                .collect(toList());
        result.setGoods(goods);
        return result;
    }

    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    public ShoppingCartData addToShoppingCart(ShoppingCartController.AddToShoppingCartRequest request,
                                              long userId) {
        List<Long> goodsId = request.getGoods()
                .stream()
                .map(ShoppingCartController.AddToShoppingCartItem::getId)
                .collect(toList());

        if (goodsId.isEmpty()) {
            throw HttpException.badRequest("商品ID为空！");
        }

        Map<Long, Goods> idToGoodsMap = goodsService.getIdToGoodsMap(goodsId);

        if (idToGoodsMap.values().stream().map(Goods::getShopId).collect(toSet()).size() != 1) {
            logger.debug("非法请求：{}, {}", goodsId, idToGoodsMap.values());
            throw HttpException.badRequest("商品ID非法！");
        }

        List<ShoppingCart> shoppingCartRows = request.getGoods()
                .stream()
                .map(item -> toShoppingCartRow(item, idToGoodsMap))
                .filter(Objects::nonNull)
                .collect(toList());

        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            ShoppingCartMapper mapper = sqlSession.getMapper(ShoppingCartMapper.class);
            shoppingCartRows.forEach(mapper::insert);
            sqlSession.commit();
        }

        return getLatestShoppingCartDataByUserIdShopId(new ArrayList<>(idToGoodsMap.values()).get(0).getShopId(), userId);
    }

    private ShoppingCartData getLatestShoppingCartDataByUserIdShopId(long shopId, long userId) {
        List<ShoppingCartData> resultRows = shoppingCartQueryMapper.selectShoppingCartDataByUserIdShopId(userId, shopId);
        return merge(resultRows);
    }

    private ShoppingCart toShoppingCartRow(ShoppingCartController.AddToShoppingCartItem item,
                                           Map<Long, Goods> idToGoodsMap) {

        Goods goods = idToGoodsMap.get(item.getId());
        if (goods == null) {
            return null;
        }

        ShoppingCart result = new ShoppingCart();
        result.setGoodsId(item.getId());
        result.setNumber(item.getNumber());
        result.setUserId(UserContext.getCurrentUser().getId());
        result.setShopId(goods.getShopId());
        result.setStatus(DataStatus.OK.toString().toLowerCase());
        result.setCreatedAt(new Date());
        result.setUpdatedAt(new Date());
        return result;
    }

    public ShoppingCartData deleteGoodsInShoppingCart(long goodsId, long userId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        if (goods == null) {
            throw HttpException.notFound("商品未找到：" + goodsId);
        }
        shoppingCartQueryMapper.deleteShoppingCart(goodsId, userId);
        return getLatestShoppingCartDataByUserIdShopId(goods.getShopId(), userId);
    }
}
