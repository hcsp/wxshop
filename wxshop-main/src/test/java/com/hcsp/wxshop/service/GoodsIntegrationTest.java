package com.hcsp.wxshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hcsp.wxshop.WxshopApplication;
import com.hcsp.wxshop.entity.Response;
import com.hcsp.wxshop.generate.Goods;
import com.hcsp.wxshop.generate.Shop;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class GoodsIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testCreateGoods() throws JsonProcessingException {
        UserLoginResponse loginResponse = loginAndGetCookie();

        Shop shop = new Shop();
        shop.setName("我的微信店铺");
        shop.setDescription("我的小店开张啦");
        shop.setImgUrl("http://shopUrl");

        HttpResponse shopResponse = doHttpRequest(
                "/api/v1/shop",
                "POST",
                shop,
                loginResponse.cookie);
        Response<Shop> shopInResponse = objectMapper.readValue(shopResponse.body, new TypeReference<Response<Shop>>() {
        });

        assertEquals(SC_CREATED, shopResponse.code);
        assertEquals("我的微信店铺", shopInResponse.getData().getName());
        assertEquals("我的小店开张啦", shopInResponse.getData().getDescription());
        assertEquals("http://shopUrl", shopInResponse.getData().getImgUrl());
        assertEquals("ok", shopInResponse.getData().getStatus());
        assertEquals(shopInResponse.getData().getOwnerUserId(), loginResponse.user.getId());


        Goods goods = new Goods();
        goods.setName("肥皂");
        goods.setDescription("纯天然无污染肥皂");
        goods.setDetails("这是一块好肥皂");
        goods.setImgUrl("http://url");
        goods.setPrice(1000L);
        goods.setStock(10);
        goods.setShopId(shopInResponse.getData().getId());

        HttpResponse response = doHttpRequest(
                "/api/v1/goods",
                "POST",
                goods,
                loginResponse.cookie);
        Response<Goods> goodsInResponse = objectMapper.readValue(response.body, new TypeReference<Response<Goods>>() {
        });

        assertEquals(SC_CREATED, response.code);
        assertEquals("肥皂", goodsInResponse.getData().getName());
        assertEquals(shopInResponse.getData().getId(), goodsInResponse.getData().getShopId());
        assertEquals("ok", goodsInResponse.getData().getStatus());
    }

    @Test
    public void return404IfGoodsToDeleteNotExist() throws JsonProcessingException {
        String cookie = loginAndGetCookie().cookie;
        HttpResponse response = doHttpRequest(
                "/api/v1/goods/12345678",
                "DELETE",
                null,
                cookie);
        assertEquals(SC_NOT_FOUND, response.code);
    }

    @Test
    public void testGetGoodsById() throws JsonProcessingException {
        UserLoginResponse loginResponse = loginAndGetCookie();
        Response<Goods> goodsResponse = doHttpRequest(
                "/api/v1/goods/2",
                "GET",
                null,
                loginResponse.cookie)
                .asJsonObject(new TypeReference<Response<Goods>>() {
                });

        assertEquals(2L, goodsResponse.getData().getId());
        assertEquals("goods2", goodsResponse.getData().getName());
        assertEquals("desc2", goodsResponse.getData().getDescription());
        assertEquals("details2", goodsResponse.getData().getDetails());
    }
}
