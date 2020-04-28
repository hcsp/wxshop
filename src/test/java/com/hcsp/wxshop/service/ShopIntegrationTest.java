package com.hcsp.wxshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hcsp.wxshop.WxshopApplication;
import com.hcsp.wxshop.entity.PageResponse;
import com.hcsp.wxshop.generate.Shop;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class ShopIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testGetShop() throws JsonProcessingException {
        UserLoginResponse loginResponse = loginAndGetCookie();


        PageResponse<Shop> shopResponse = doHttpRequest(
                "/api/v1/shop?pageNum=2&pageSize=1",
                "GET",
                null,
                loginResponse.cookie)
                .asJsonObject(new TypeReference<PageResponse<Shop>>() {
                });

        assertEquals(2, shopResponse.getTotalPage());
        assertEquals(1, shopResponse.getPageSize());
        assertEquals(2, shopResponse.getPageNum());
        assertEquals(2L, shopResponse.getData().get(0).getId());
        assertEquals("shop2", shopResponse.getData().get(0).getName());
    }
}
