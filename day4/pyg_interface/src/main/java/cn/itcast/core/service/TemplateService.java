package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;

public interface TemplateService {

    public PageResult findPage(TypeTemplate template, Integer page, Integer rows);

    public void add(TypeTemplate template);

    public void update(TypeTemplate template);

    public void delete(Long[] ids);

    public TypeTemplate findOne(Long id);
}
