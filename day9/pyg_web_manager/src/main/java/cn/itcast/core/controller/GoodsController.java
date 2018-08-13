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
 * 运营商商品管理
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



    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.dele(ids);
            return  new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            return  new Result(true, "审核成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "审核失败!");
        }
    }
}
