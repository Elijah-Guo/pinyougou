package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.GoodsEntity;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品管理
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods, Integer page, Integer rows) {
        //获取当前登录用户的用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(userName);
        PageResult pageResult = goodsService.findPage(goods, page, rows);
        return pageResult;
    }

    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id){
        GoodsEntity goodsEntity = goodsService.findOne(id);
        return goodsEntity;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody  GoodsEntity goodsEntity) {
        try {
            //获取当前登录用户的用户名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsEntity.getGoods().setSellerId(userName);
            goodsService.add(goodsEntity);
            return  new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/update")
    public Result update(@RequestBody  GoodsEntity goodsEntity) {
        try {
            //获取当前登录用户的用户名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //对比当前登录用户的用户名和页面传入进来的商品中的用户名是否相等
            if(!userName.equals(goodsEntity.getGoods().getSellerId())){
                return new Result(false, "您没有权限修改!");
            }
            //对比页面传入进来的用户名和页面传入进来的商品ID所对应的数据库中数据的用户名是否相等
            GoodsEntity dbGoodsEntity = goodsService.findOne(goodsEntity.getGoods().getId());
            if(!dbGoodsEntity.getGoods().getSellerId().equals(goodsEntity.getGoods().getSellerId())){
                return new Result(false, "您没有权限修改!");
            }

            goodsService.update(goodsEntity);
            return  new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "保存失败!");
        }
    }
}
