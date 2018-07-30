package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * 查询所有
     * @return
     */
    @RequestMapping("/findAll")
    public List<Brand> findAll() {
        List<Brand> brandList = brandService.findAll();
        return brandList;
    }

    /**
     * 分页查询
     * @param  page 当前页
     * @param  rows 每页展示数据条数
     *
     */
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer rows){
        try {
            PageResult pageResult = brandService.findPage(null, page, rows);
            return pageResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody  Brand brand) {
        try {
            brandService.add(brand);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/findOne")
    public Brand findOne(Long id) {
        Brand one = brandService.findOne(id);
        return one;
    }

    @RequestMapping("/update")
    public Result update(@RequestBody  Brand brand){
        try {
            brandService.update(brand);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody Brand brand, Integer page, Integer rows) {
        PageResult pageResult = brandService.findPage(brand, page, rows);
        return pageResult;
    }
}
