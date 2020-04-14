package com.hcsp.wxshop.controller;

import com.hcsp.wxshop.entity.HttpException;
import com.hcsp.wxshop.entity.PageResponse;
import com.hcsp.wxshop.entity.Response;
import com.hcsp.wxshop.entity.ShoppingCartData;
import com.hcsp.wxshop.service.ShoppingCartService;
import com.hcsp.wxshop.service.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ShoppingCartController {
    private static Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);

    private final ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }
    // @formatter:off
    /**
     * @api {get} /shoppingCart 获取当前用户名下的所有购物车物品
     * @apiName GetShoppingCart
     * @apiGroup 购物车
     *
     * @apiHeader {String} Accept application/json
     *
     * @apiParam {Number} pageNum 页数，从1开始
     * @apiParam {Number} pageSize 每页显示的数量
     *
     * @apiSuccess {Number} pageNum 页数，从1开始
     * @apiSuccess {Number} pageSize 每页显示的数量
     * @apiSuccess {Number} totalPage 共有多少页
     * @apiSuccess {ShoppingCart} data 购物车物品列表，按照店铺分组
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "pageNum": 1,
     *       "pageSize": 10,
     *       "totalPage": 5,
     *       "data": [
     *         {
     *           "shop": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *            },
     *            "goods": [
     *              {
     *                  "id": 12345,
     *                  "name": "肥皂",
     *                  "description": "纯天然无污染肥皂",
     *                  "details": "这是一块好肥皂",
     *                  "imgUrl": "https://img.url",
     *                  "price": 500,
     *                  "number": 10,
     *                  "createdAt": "2020-03-22T13:22:03Z",
     *                  "updatedAt": "2020-03-22T13:22:03Z"
     *              },
     *              {
     *                    ...
     *              }
     *           ]
     *         }
     *       ]
     *     }
     *
     * @apiError 401 Unauthorized 若用户未登录
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    /**
     *
     * @param pageNum 页码
     * @param pageSize 每页元素数量
     * @return 结果
     */
    // @formatter:on
    @GetMapping("/shoppingCart")
    public PageResponse<ShoppingCartData> getShoppingCart(
            @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int pageSize
    ) {
        return shoppingCartService.getShoppingCartOfUser(UserContext.getCurrentUser().getId(),
                pageNum,
                pageSize);
    }


    // @formatter:off
    /**
     * @api {post} /shoppingCart 加购物车
     * @apiName AddShoppingCart
     * @apiGroup 购物车
     *
     * @apiHeader {String} Accept application/json
     * @apiHeader {String} Content-Type application/json
     * @apiParamExample {json} Request-Example:
     *            {
     *              "goods": [
     *                {
     *                    "id": 12345,
     *                    "number": 10,
     *                },
     *                {
     *                    ...
     *                }
     *            }
     *
     * @apiSuccess {ShoppingCart} data 更新后的该店铺物品列表
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "data": {
     *           "shop": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *            },
     *            "goods": [
     *              {
     *                  "id": 12345,
     *                  "name": "肥皂",
     *                  "description": "纯天然无污染肥皂",
     *                  "details": "这是一块好肥皂",
     *                  "imgUrl": "https://img.url",
     *                  "address": "XXX",
     *                  "price": 500,
     *                  "number": 10,
     *                  "createdAt": "2020-03-22T13:22:03Z",
     *                  "updatedAt": "2020-03-22T13:22:03Z"
     *              },
     *              {
     *                    ...
     *              }
     *           ]
     *         }
     *       }
     *     }
     *
     * @apiError 400 Bad Request 若用户的请求中包含错误
     * @apiError 401 Unauthorized 若用户未登录
     * @apiError 404 Not Found 若商品未找到
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    /**
     *
     * @param request 加购物车请求
     * @return 添加后的结果
     */
    // @formatter:on
    @PostMapping("/shoppingCart")
    public Response<ShoppingCartData> addToShoppingCart(@RequestBody AddToShoppingCartRequest request) {
        try {
            return Response.of(shoppingCartService.addToShoppingCart(request));
        } catch (HttpException e) {
            return Response.of(e.getMessage(), null);
        }
    }


    public static class AddToShoppingCartRequest {
        List<AddToShoppingCartItem> goods;

        public List<AddToShoppingCartItem> getGoods() {
            return goods;
        }

        public void setGoods(List<AddToShoppingCartItem> goods) {
            this.goods = goods;
        }
    }

    public static class AddToShoppingCartItem {
        long id;
        int number;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }

    // @formatter:off
    /**
     * @api {delete} /shoppingCart/:goodsId 删除当前用户购物车中指定的商品
     * @apiName DeleteShoppingCart
     * @apiGroup 购物车
     *
     * @apiHeader {String} Accept application/json
     *
     * @apiParam {Number} goodsId 要删除的商品ID
     *
     * @apiSuccess {ShoppingCart} data 更新后的该店铺物品列表
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "data": {
     *           "shop": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *            },
     *            "goods": [
     *              {
     *                  "id": 12345,
     *                  "name": "肥皂",
     *                  "description": "纯天然无污染肥皂",
     *                  "details": "这是一块好肥皂",
     *                  "imgUrl": "https://img.url",
     *                  "address": "XXX",
     *                  "price": 500,
     *                  "number": 10,
     *                  "createdAt": "2020-03-22T13:22:03Z",
     *                  "updatedAt": "2020-03-22T13:22:03Z"
     *              },
     *              {
     *                    ...
     *              }
     *           ]
     *         }
     *       }
     *     }
     *
     * @apiError 401 Unauthorized 若用户未登录
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    // @formatter:on
    public void deleteShoppingCart() {
    }


}
