package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.GoodsEntity;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.CmsService;
import cn.itcast.core.service.GoodsService;
import cn.itcast.core.service.ItemManagerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 运营商商品管理
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemManagerService itemManagerService;

    @Reference
    private CmsService cmsService;

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
            if (ids != null) {
                for (Long id : ids) {
                    //1. 根据商品id到数据库中将商品逻辑删除
                    goodsService.dele(id);
                    //2. 根据商品id删除solr索引库对应的数据
                    itemManagerService.delItemFromSolr(id);
                }
            }
            return  new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            if (ids != null) {
                for (Long id : ids) {
                    //1. 根据商品ID改变数据库中商品的上架状态
                    goodsService.updateStatus(id, status);

                    //审核通过时进行下面的业务操作
                    if("1".equals(status)){
                        //2. 将商品放入solr索引库中供前台系统搜索
                        itemManagerService.itemToSolr(id);

                        //3. 将根据商品详细数据生成商品详情的静态化页面
                        Map<String, Object> rootMap = cmsService.findGoods(id);
                        cmsService.createStaticPage(id, rootMap);
                    }
                }
            }
            return  new Result(true, "审核成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "审核失败!");
        }
    }

    /**
     * 测试生成静态化页面
     * @param goodsid
     * @return
     */
    @RequestMapping("/testHtml")
    public Result genHtml(Long goodsid) {
        try {
            Map<String, Object> rootMap = cmsService.findGoods(goodsid);
            cmsService.createStaticPage(goodsid, rootMap);
            return new Result(true, "生成成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "生成失败!");
        }
    }
}
