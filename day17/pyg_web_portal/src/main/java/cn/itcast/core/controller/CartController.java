package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Cart;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.service.CartService;
import cn.itcast.core.util.Constants;
import cn.itcast.core.util.CookieUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 购物车controller
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;


    /**
     * 将商品加入到购物车中
     * @param itemId    购买商品的库存id
     * @param num       购买数量
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    //crossorigin指定的路径是跳转到哪个系统页面的路径
    @CrossOrigin(origins="http://localhost:8085",allowCredentials="true")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        try {
            //1. 获取当前登录用户名称
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //2. 获取购物车列表
            List<Cart> cartList = findCartList();
            //3. 将当前商品加入到购物车列表, 返回最新购物车列表
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            //声明购物车列表字符串
            String cartListStr = "[]";
            if(cartList != null && cartList.size() > 0){
                cartListStr = JSON.toJSONString(cartList);
            }
            //4. 判断当前用户是否登录, 未登录用户名为"anonymousUser"
            if ("anonymousUser".equals(userName)) {
                //4.a.如果未登录, 则将购物车列表存入cookie中
                CookieUtil.setCookie(request, response, Constants.CART_COOKIE_KEY, cartListStr, 3600 * 24 * 30, "utf-8");
            } else {
                //4.b.如果已登录, 则将购物车列表存入redis中
                cartService.setCartListToReids(cartList, userName);
            }
            return new Result(true, "添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败!");
        }

    }


    /**
     * 获取购物车列表
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //1. 获取当前登录用户名称
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //2. 从cookie中获取购物车列表json格式字符串
        String cartListStr = CookieUtil.getCookieValue(request, Constants.CART_COOKIE_KEY, "utf-8");
        //3. 如果购物车列表json串为空则返回"[]"
        if (null == cartListStr || "".equals(cartListStr)) {
            cartListStr = "[]";
        }
        //4. 将购物车列表json转换为对象
        List<Cart> cookieCartList = JSON.parseArray(cartListStr, Cart.class);
        //5. 判断用户是否登录, 未登录用户为"anonymousUser"
        if ("anonymousUser".equals(userName)) {
            //5.a. 未登录, 返回cookie中的购物车列表对象
            return cookieCartList;
        } else {
            //5.b.1.已登录, 从redis中获取购物车列表对象
            List<Cart> redisCartList = cartService.getCartListFromRedis(userName);
            //5.b.2.判断cookie中是否存在购物车列表
            if (!"[]".equals(cartListStr)) {
                //如果cookie中存在购物车列表则和redis中的购物车列表合并成一个对象
                redisCartList = cartService.mergeCartList(redisCartList, cookieCartList);
                //删除cookie中购物车列表
                CookieUtil.deleteCookie(request, response,  Constants.CART_COOKIE_KEY);
                //将合并后的购物车列表存入redis中
                cartService.setCartListToReids(redisCartList, userName);
            }
            //5.b.3.返回购物车列表对象
            return redisCartList;
        }
    }
}
