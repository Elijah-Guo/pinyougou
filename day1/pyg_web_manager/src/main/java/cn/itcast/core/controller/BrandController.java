package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 品牌管理
 */

//@RestController注解的作用就是相当于在类上面加上@Controller注解并且相当于在这个类下面的所有方法上都加上@ResponseBody注解
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<Brand> findAll() {
        List<Brand> brandList = brandService.findAll();
        return brandList;
    }
}
