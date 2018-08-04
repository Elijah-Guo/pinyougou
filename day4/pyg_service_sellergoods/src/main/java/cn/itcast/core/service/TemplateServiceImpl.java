package cn.itcast.core.service;

import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService{

    @Autowired
    private TypeTemplateDao templateDao;

    @Override
    public PageResult findPage(TypeTemplate template, Integer page, Integer rows) {
        TypeTemplateQuery query = new TypeTemplateQuery();
        if(template != null){
            TypeTemplateQuery.Criteria criteria = query.createCriteria();
            if(template.getName() != null && !"".equals(template.getName())){
                criteria.andNameLike("%"+template.getName()+"%");
            }
        }
        PageHelper.startPage(page, rows);
        Page<TypeTemplate> pageList = (Page<TypeTemplate>)templateDao.selectByExample(query);
        return new PageResult(pageList.getTotal(), pageList.getResult());
    }

    @Override
    public void add(TypeTemplate template) {
        templateDao.insertSelective(template);
    }

    @Override
    public void update(TypeTemplate template) {
        templateDao.updateByPrimaryKeySelective(template);
    }

    @Override
    public void delete(Long[] ids) {
        if(ids != null){
            for(Long id : ids){
                templateDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public TypeTemplate findOne(Long id) {
        TypeTemplate typeTemplate = templateDao.selectByPrimaryKey(id);
        return typeTemplate;
    }
}
