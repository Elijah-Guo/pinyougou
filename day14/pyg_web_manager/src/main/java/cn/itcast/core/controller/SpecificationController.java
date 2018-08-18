package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.entity.SpecEntity;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 规格管理
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody  Specification spec, Integer page, Integer rows ) {
        PageResult pageResult = specService.findPage(spec, page, rows);
        return pageResult;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody SpecEntity spec){
        try {
            specService.add(spec);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/findOne")
    public SpecEntity findOne(Long id){
        SpecEntity one = specService.findOne(id);
        return one;
    }

    @RequestMapping("/update")
    public Result update(@RequestBody SpecEntity spec){
        try {
            specService.update(spec);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            specService.delete(ids);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        List<Map> maps = specService.selectOptionList();
        return maps;
    }
}
