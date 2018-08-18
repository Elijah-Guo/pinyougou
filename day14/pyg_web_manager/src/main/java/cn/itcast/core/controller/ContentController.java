package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.service.ContentCategoryService;
import cn.itcast.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 广告管理
 */

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;


    /**
     * 查询所有
     * @return
     */
    @RequestMapping("/findAll")
    public List<Content> findAll() {
        List<Content> list = contentService.findAll();
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
            PageResult pageResult = contentService.findPage(null, page, rows);
            return pageResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody  Content content) {
        try {
            contentService.add(content);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/findOne")
    public Content findOne(Long id) {
        Content one = contentService.findOne(id);
        return one;
    }

    @RequestMapping("/update")
    public Result update(@RequestBody  Content content){
        try {
            contentService.update(content);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            contentService.delete(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody Content content, Integer page, Integer rows) {
        PageResult pageResult = contentService.findPage(content, page, rows);
        return pageResult;
    }

}
