package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import cn.itcast.core.service.ContentCategoryService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 广告分类管理
 */

@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {

    @Reference
    private ContentCategoryService categoryService;


    /**
     * 查询所有
     * @return
     */
    @RequestMapping("/findAll")
    public List<ContentCategory> findAll() {
        List<ContentCategory> list = categoryService.findAll();
        return list;
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
            PageResult pageResult = categoryService.findPage(null, page, rows);
            return pageResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody  ContentCategory category) {
        try {
            categoryService.add(category);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/findOne")
    public ContentCategory findOne(Long id) {
        ContentCategory one = categoryService.findOne(id);
        return one;
    }

    @RequestMapping("/update")
    public Result update(@RequestBody  ContentCategory category){
        try {
            categoryService.update(category);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            categoryService.delete(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody ContentCategory category, Integer page, Integer rows) {
        PageResult pageResult = categoryService.findPage(category, page, rows);
        return pageResult;
    }

}
