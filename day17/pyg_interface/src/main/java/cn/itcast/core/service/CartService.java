package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.Cart;

import java.util.List;

public interface CartService {

    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);


    public void setCartListToReids(List<Cart> cartList, String userId);

    public List<Cart> getCartListFromRedis(String userId);

    public List<Cart> mergeCartList(List<Cart> redisCartList, List<Cart> cookieCartList);
}
