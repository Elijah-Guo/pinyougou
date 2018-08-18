package cn.itcast.core.service;

import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface ContentCategoryService {

    public List<ContentCategory> findAll();

    public PageResult findPage(ContentCategory category, Integer page, Integer rows);

    public void add(ContentCategory category);

    public ContentCategory findOne(Long id);

    public void update(ContentCategory category);

    public void delete(Long[] ids);


}
