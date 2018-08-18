package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家审核管理
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    /**
     * 分页高级查询
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody Seller seller, Integer page, Integer rows){
        PageResult pageResult = sellerService.findPage(seller, page, rows);
        return pageResult;
    }

    @RequestMapping("/findOne")
    public Seller findOne(String id){
        Seller one = sellerService.findOne(id);
        return one;
    }

    /**
     * 商家审核
     * @param sellerId   需要审核的商家id
     * @param status     审核状态
     * @return
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId, String status) {
        try {
            sellerService.updateStatus(sellerId, status);
            return new Result(true, "审核成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "审核失败!");
        }
    }
}
