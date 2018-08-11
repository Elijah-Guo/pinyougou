package cn.itcast.core.service;

import cn.itcast.core.dao.ad.ContentCategoryDao;
import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.ad.ContentCategoryQuery;
import cn.itcast.core.pojo.ad.ContentQuery;
import cn.itcast.core.pojo.entity.PageResult;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContentServiceImpl implements ContentService{

    @Autowired
    private ContentDao contentDao;


    @Override
    public List<Content> findAll() {
        List<Content> contents = contentDao.selectByExample(null);
        return contents;
    }

    @Override
    public PageResult findPage(Content content, Integer page, Integer rows) {
        ContentQuery query = new ContentQuery();
        //根据字段中的内容排序, 这里是降序
        query.setOrderByClause("sort_order desc");
        PageHelper.startPage(page, rows);
        Page<Content> pageList = (Page<Content>)contentDao.selectByExample(query);
        return new PageResult(pageList.getTotal(), pageList.getResult());
    }

    @Override
    public void add(Content content) {
        contentDao.insertSelective(content);
    }

    @Override
    public Content findOne(Long id) {
        Content content = contentDao.selectByPrimaryKey(id);
        return content;
    }

    @Override
    public void update(Content content) {
        contentDao.updateByPrimaryKeySelective(content);
    }

    @Override
    public void delete(Long[] ids) {
        if(ids != null){
            for(Long id : ids){
                contentDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        ContentQuery query = new ContentQuery();
        //按照排序字段降序排列
        query.setOrderByClause("sort_order desc");
        ContentQuery.Criteria criteria = query.createCriteria();
        //根据外键查询
        criteria.andCategoryIdEqualTo(categoryId);
        //查询状态为1的
        criteria.andStatusEqualTo("1");
        List<Content> contents = contentDao.selectByExample(query);
        return contents;
    }
}
