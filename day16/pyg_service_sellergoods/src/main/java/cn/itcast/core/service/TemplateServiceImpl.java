package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService{

    @Autowired
    private TypeTemplateDao templateDao;

    @Autowired
    private SpecificationOptionDao optionDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult findPage(TypeTemplate template, Integer page, Integer rows) {
        //查询所有模板数据
        List<TypeTemplate> templateList = templateDao.selectByExample(null);
        if(templateList != null){
            for(TypeTemplate temp : templateList){
                /**
                 * 缓存品牌集合数据
                 */
                List<Map> brandList = JSON.parseArray(temp.getBrandIds(), Map.class);
                redisTemplate.boundHashOps(Constants.BRAND_REDIS).put(temp.getId(), brandList);

                /**
                 * 缓存规格集合数据
                 */
                List<Map> specList = findSpecList(temp.getId());
                redisTemplate.boundHashOps(Constants.SPEC_REDIS).put(temp.getId(), specList);


            }
        }


        /**
         * 查询模板列表数据
         */
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

    /**
     * 根据模板id, 查询对应的规格和规格选项数据
     * @param id    模板id
     * @return
     */
    @Override
    public List<Map> findSpecList(Long id) {
        //1. 根据模板id, 获取模板数据
        TypeTemplate typeTemplate = templateDao.selectByPrimaryKey(id);
        //2. 将获取到的规格的基本数据, 转换成对象, 原来是json字符串
        List<Map> maps = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
        //3. 根据规格id获取规格选项数据, 并且拼接到这个规格集合中
        if(maps != null){
            for(Map map : maps){
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(Long.parseLong(String.valueOf(map.get("id"))));
                List<SpecificationOption> options = optionDao.selectByExample(query);
                map.put("options", options);
            }
        }
        return maps;
    }
}
