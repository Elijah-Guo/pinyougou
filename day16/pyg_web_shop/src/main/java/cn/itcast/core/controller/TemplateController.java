package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.TemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 模板管理
 */
@RestController
@RequestMapping("/typeTemplate")
public class TemplateController {

    @Reference
    private TemplateService templateService;


    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        TypeTemplate one = templateService.findOne(id);
        return one;
    }

    /**
     * 根据模板id获取规格详细数据
     * [
         {
         "options":[
                {"id":98,"optionName":"移动3G","orders":1,"specId":27},
                {"id":99,"optionName":"移动4G","orders":2,"specId":27}
         ],
        "id":27,
        "text":"网络"},
        {
         "options":[
                 {"id":118,"optionName":"16G","orders":1,"specId":32},
                 {"id":119,"optionName":"32G","orders":2,"specId":32}
         ],
         "id":32,
         "text":"机身内存"
         }
     ]
     * @param id   模板id
     * @return
     */
    @RequestMapping("/findBySpecList")
    public List<Map> findBySpecList(Long id) {
        List<Map> specList = templateService.findSpecList(id);
        return specList;
    }

}
