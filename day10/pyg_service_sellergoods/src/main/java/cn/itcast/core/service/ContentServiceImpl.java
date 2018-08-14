package cn.itcast.core.service;

import cn.itcast.core.dao.ad.ContentCategoryDao;
import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.ad.ContentCategoryQuery;
import cn.itcast.core.pojo.ad.ContentQuery;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContentServiceImpl implements ContentService{

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private RedisTemplate redisTemplate;


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
        //根据分类id, 删除缓存中对应的广告内容.
        redisTemplate.boundHashOps(Constants.CONTENT_REDIS).delete(content.getCategoryId());
    }

    @Override
    public Content findOne(Long id) {
        Content content = contentDao.selectByPrimaryKey(id);
        return content;
    }

    @Override
    public void update(Content content) {
        //1. 根据广告id, 到数据库查询老的广告对象
        Content oldContent = contentDao.selectByPrimaryKey(content.getId());
        //2. 根据新的分类id, 到redis中清除对应的数据
        redisTemplate.boundHashOps(Constants.CONTENT_REDIS).delete(content.getCategoryId());
        //3. 根据老的分类id, 到redis中清除对应的数据
        redisTemplate.boundHashOps(Constants.CONTENT_REDIS).delete(oldContent.getCategoryId());
        //4. 到数据库中执行修改操作
        contentDao.updateByPrimaryKeySelective(content);
    }

    @Override
    public void delete(Long[] ids) {
        if(ids != null){
            for(Long id : ids){
                //1. 根据广告id, 到数据库查询广告对象
                Content content = contentDao.selectByPrimaryKey(id);
                //2. 根据广告id, 删除广告在数据库中的内容
                contentDao.deleteByPrimaryKey(id);
                //3. 根据广告分类id, 删除缓存中的对应的数据
                redisTemplate.boundHashOps(Constants.CONTENT_REDIS).delete(content.getCategoryId());
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

    /**
     * 存入redis中广告的数据格式
     * content_redis   :
     *                  分类id    这个分类的广告集合
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Content> findByCategoryIdFromRedis(Long categoryId) {
        //根据分类id从redis中获取广告集合数据
        List<Content> contentList = (List<Content>)redisTemplate.boundHashOps(Constants.CONTENT_REDIS).get(categoryId);
        if (contentList == null) {
            //如果redis中获取数据为空, 则从数据库获取数据
            contentList = findByCategoryId(categoryId);
            //将从数据库获取的数据存入redis
            redisTemplate.boundHashOps(Constants.CONTENT_REDIS).put(categoryId, contentList);
        }

        return contentList;
    }
}
