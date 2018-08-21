package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.entity.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements  CartService{

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemDao itemDao;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1. 根据商品SKU ID查询SKU商品信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        //2. 判断商品是否存在不存在, 抛异常
        if (item == null) {
            throw new RuntimeException("库存ID不存在!");
        }
        //3. 判断商品状态是否为1已审核, 状态不对抛异常
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("商品未审核通过!");
        }
        //4.获取商家ID
        String sellerId = item.getSellerId();
        //5.根据商家ID判断购物车列表中是否存在该商家的购物车
        Cart cart = findCartFromCartListBySellerId(cartList, sellerId);
        //6.如果购物车列表中不存在该商家的购物车
        if (null == cart) {
            //6.a.1 新建购物车对象
            cart = new Cart();
            //6.a.2 将新建的购物车对象添加到购物车列表
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            //新建购物项集合
            List<OrderItem> orderItemList = new ArrayList<OrderItem>();
            //新建购物项对象
            OrderItem orderItem = createOrderItem(item, num);
            //将购物项加入到购物项集合中
            orderItemList.add(orderItem);
            //将购物项集合加入到购物车中
            cart.setOrderItemList(orderItemList);
            //将新建的购物车对象加入到购物车集合中
            cartList.add(cart);
        } else {
            //6.b.1如果购物车列表中存在该商家的购物车, //查询购物车明细列表中是否存在该商品
            OrderItem orderItem = findOrderItemFromListByItemId(cart.getOrderItemList(), itemId);
            //6.b.2判断购物车明细是否为空
            if (orderItem == null) {
                //6.b.3为空，新增购物车明细
                orderItem = createOrderItem(item, num);
                //将购物项加入到购物项集合中
                cart.getOrderItemList().add(orderItem);
                cartList.add(cart);
            } else {
                //6.b.4不为空，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()).multiply(orderItem.getPrice()));
                //6.b.5如果购物车明细中数量操作后小于等于0，则移除
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);
                }
                //6.b.6如果购物车中购物车明细列表为空,则移除
                if (cart.getOrderItemList() == null || cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            }
        }


        //7. 返回购物车列表对象
        return cartList;
    }

    /**
     * 从购物项列表中查找指定的商品
     * @param orderItemList   购物项列表
     * @param itemId          商品库存id
     * @return
     */
    private OrderItem findOrderItemFromListByItemId(List<OrderItem> orderItemList, Long itemId) {
        if (orderItemList != null) {
            for (OrderItem orderItem : orderItemList){
                if (itemId.equals(orderItem.getItemId())){
                    return orderItem;
                }
            }
        }
        return null;
    }

    /**
     * 新建购物项对象
     * @param item    库存对象
     * @param num     购买数量
     * @return
     */
    private OrderItem createOrderItem(Item item, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("购买数量错误!");
        }
        OrderItem orderItem = new OrderItem();
        //商品id
        orderItem.setGoodsId(item.getGoodsId());
        //库存id
        orderItem.setItemId(item.getId());
        //购买数量
        orderItem.setNum(num);
        //图片路径
        orderItem.setPicPath(item.getImage());
        //单价
        orderItem.setPrice(item.getPrice());
        //卖家id
        orderItem.setSellerId(item.getSellerId());
        //商品名称
        orderItem.setTitle(item.getTitle());
        //总价
        orderItem.setTotalFee(new BigDecimal(num).multiply(item.getPrice()));
        return orderItem;
    }

    /**
     * 从购物车列表中查找指定商家的购物车对象, 如果找不到返回一个新的购物车对象
     * @param cartList      购物车列表
     * @param sellerId      商家id
     * @return
     */
    private Cart findCartFromCartListBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList != null) {
            for (Cart cart : cartList){
                if(cart.getSellerId().equals(sellerId)){
                    return cart;
                }
            }
        }
        return  null;
    }

    @Override
    public void setCartListToReids(List<Cart> cartList, String userId) {
        if (cartList == null) {
            cartList = new ArrayList<Cart>();
        }
        redisTemplate.boundHashOps(Constants.CART_REDIS_KEY).put(userId, cartList);
    }

    @Override
    public List<Cart> getCartListFromRedis(String userId) {
        List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps(Constants.CART_REDIS_KEY).get(userId);
        if (cartList == null) {
            cartList  = new ArrayList<Cart>();
        }
        return cartList;
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> redisCartList, List<Cart> cookieCartList) {
        for(Cart cookieCart : cookieCartList){
            for(OrderItem cookieOrderItem : cookieCart.getOrderItemList()){
                addGoodsToCartList(redisCartList, cookieOrderItem.getItemId(), cookieOrderItem.getNum());
            }
        }
        return redisCartList;
    }
}
