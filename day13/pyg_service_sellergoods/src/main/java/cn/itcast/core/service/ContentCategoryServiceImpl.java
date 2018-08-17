package cn.itcast.core.service;

import cn.itcast.core.dao.ad.ContentCategoryDao;
import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.ad.ContentCategoryQuery;
import cn.itcast.core.pojo.entity.PageResult;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContentCategoryServiceImpl implements ContentCategoryService{

    @Autowired
    private ContentCategoryDao categoryDao;

    @Override
    public List<ContentCategory> findAll() {
        List<ContentCategory> contentCategories = categoryDao.selectByExample(null);
        return contentCategories;
    }

    @Override
    public PageResult findPage(ContentCategory category, Integer page, Integer rows) {
        ContentCategoryQuery query = new ContentCategoryQuery();
        if(category != null){
            ContentCategoryQuery.Criteria criteria = query.createCriteria();
            if(category.getName() != null && !"".equals(category.getName())){
                criteria.andNameLike("%"+category.getName()+"%");
            }
        }
        PageHelper.startPage(page, rows);
        Page<ContentCategory> pageList = (Page<ContentCategory>) categoryDao.selectByExample(query);
        return new PageResult(pageList.getTotal(), pageList.getResult());
    }

    @Override
    public void add(ContentCategory category) {
        categoryDao.insertSelective(category);
    }

    @Override
    public ContentCategory findOne(Long id) {
        ContentCategory contentCategory = categoryDao.selectByPrimaryKey(id);
        return contentCategory;
    }

    @Override
    public void update(ContentCategory category) {
        categoryDao.updateByPrimaryKeySelective(category);
    }

    @Override
    public void delete(Long[] ids) {
        if(ids != null){
            for(Long id : ids){
                categoryDao.deleteByPrimaryKey(id);
            }
        }
    }
}
