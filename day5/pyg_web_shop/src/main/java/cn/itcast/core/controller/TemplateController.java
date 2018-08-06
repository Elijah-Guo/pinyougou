package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.TemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
