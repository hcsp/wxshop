package com.hcsp.wxshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Sets;
import com.hcsp.wxshop.WxshopApplication;
import com.hcsp.wxshop.controller.ShoppingCartController;
import com.hcsp.wxshop.entity.DataStatus;
import com.hcsp.wxshop.entity.PageResponse;
import com.hcsp.wxshop.entity.Response;
import com.hcsp.wxshop.entity.ShoppingCartData;
import com.hcsp.wxshop.entity.ShoppingCartGoods;
import com.hcsp.wxshop.generate.Goods;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class ShoppingCartIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void canQueryShoppingCartData() throws JsonProcessingException {
        UserLoginResponse loginResponse = loginAndGetCookie();
        PageResponse<ShoppingCartData> response = doHttpRequest("/api/v1/shoppingCart?pageNum=2&pageSize=1",
                "GET", null, loginResponse.cookie).asJsonObject(new TypeReference<PageResponse<ShoppingCartData>>() {
        });

        Assertions.assertEquals(2, response.getPageNum());
        Assertions.assertEquals(1, response.getPageSize());
        Assertions.assertEquals(2, response.getTotalPage());
        Assertions.assertEquals(1, response.getData().size());
        Assertions.assertEquals(2, response.getData().get(0).getShop().getId());
        Assertions.assertEquals(Arrays.asList(4L, 5L),
                response.getData().get(0).getGoods().stream()
                        .map(Goods::getId).collect(toList()));
        Assertions.assertEquals(Arrays.asList(100L, 200L),
                response.getData().get(0).getGoods().stream()
                        .map(ShoppingCartGoods::getPrice).collect(toList()));
        Assertions.assertEquals(Arrays.asList(200, 300),
                response.getData().get(0).getGoods().stream()
                        .map(ShoppingCartGoods::getNumber).collect(toList()));
    }

    @Test
    public void canAddShoppingCartData() throws Exception {
        UserLoginResponse loginResponse = loginAndGetCookie();

        ShoppingCartController.AddToShoppingCartRequest request = new ShoppingCartController.AddToShoppingCartRequest();
        ShoppingCartController.AddToShoppingCartItem item = new ShoppingCartController.AddToShoppingCartItem();
        item.setId(2L);
        item.setNumber(2);

        request.setGoods(Collections.singletonList(item));

        Response<ShoppingCartData> response = doHttpRequest("/api/v1/shoppingCart",
                "POST", request, loginResponse.cookie).asJsonObject(new TypeReference<Response<ShoppingCartData>>() {
        });

        Assertions.assertEquals(1L, response.getData().getShop().getId());
        Assertions.assertEquals(Arrays.asList(1L, 2L),
                response.getData().getGoods().stream().map(Goods::getId).collect(toList()));
        Assertions.assertEquals(Sets.newHashSet(2, 100),
                response.getData().getGoods().stream().map(ShoppingCartGoods::getNumber).collect(toSet()));
        Assertions.assertTrue(response.getData().getGoods().stream().allMatch(
                goods -> goods.getShopId() == 1L
        ));
    }

    // 重复将同一个商品加入购物车，后面的商品会覆盖前面的
    @Test
    public void addingSameGoodsToShoppingCartOverwritesOldGoods() throws Exception {
        // 第一次添加id为2的商品，2个
        canAddShoppingCartData();

        UserLoginResponse loginResponse = loginAndGetCookie();

        // 第二次添加id为2的商品，1个
        ShoppingCartController.AddToShoppingCartRequest request = new ShoppingCartController.AddToShoppingCartRequest();
        ShoppingCartController.AddToShoppingCartItem item = new ShoppingCartController.AddToShoppingCartItem();
        item.setId(2L);
        item.setNumber(1);

        request.setGoods(Collections.singletonList(item));

        Response<ShoppingCartData> addShoppingCartResponse = doHttpRequest("/api/v1/shoppingCart",
                "POST", request, loginResponse.cookie).asJsonObject(new TypeReference<Response<ShoppingCartData>>() {
        });

        PageResponse<ShoppingCartData> getShoppingCartResponse = doHttpRequest("/api/v1/shoppingCart?pageNum=1&pageSize=100",
                "GET", null, loginResponse.cookie).asJsonObject(new TypeReference<PageResponse<ShoppingCartData>>() {
        });

        ShoppingCartData shop1Data = getShoppingCartResponse.getData().stream().filter(data -> data.getShop().getId() == 1)
                .findFirst().get();

        Assertions.assertEquals(Arrays.asList(1L, 2L),
                shop1Data.getGoods().stream().map(Goods::getId).collect(toList()));
        Assertions.assertEquals(Sets.newHashSet(1, 100),
                shop1Data.getGoods().stream().map(ShoppingCartGoods::getNumber).collect(toSet()));
    }

    @Test
    public void canDeleteShoppingCartData() throws Exception {
        UserLoginResponse loginResponse = loginAndGetCookie();

        Response<ShoppingCartData> response = doHttpRequest("/api/v1/shoppingCart/5",
                "DELETE", null, loginResponse.cookie).asJsonObject(new TypeReference<Response<ShoppingCartData>>() {
        });

        Assertions.assertEquals(2L, response.getData().getShop().getId());

        Assertions.assertEquals(1, response.getData().getGoods().size());
        ShoppingCartGoods goods = response.getData().getGoods().get(0);

        Assertions.assertEquals(4L, goods.getId());
        Assertions.assertEquals(200, goods.getNumber());
        Assertions.assertEquals(DataStatus.OK.toString().toLowerCase(), goods.getStatus());
    }
}
