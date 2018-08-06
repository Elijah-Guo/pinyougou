package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.SpecEntity;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService{

    @Autowired
    private SpecificationDao specDao;

    @Autowired
    private SpecificationOptionDao optionDao;

    @Override
    public PageResult findPage(Specification spec, Integer page, Integer rows) {
        SpecificationQuery query = new SpecificationQuery();
        if(spec != null){
            SpecificationQuery.Criteria criteria = query.createCriteria();
            if (spec.getSpecName() != null && !"".equals(spec.getSpecName())){
                criteria.andSpecNameLike("%"+spec.getSpecName()+"%");
            }
        }

        PageHelper.startPage(page, rows);
        Page<Specification> pageList = (Page<Specification>)specDao.selectByExample(query);
        return new PageResult(pageList.getTotal(), pageList.getResult());
    }

    @Override
    public void add(SpecEntity spec) {
        //1. 添加规格数据
        //更改映射文件useGeneratedKeys="true"属性表示使用自增主键  keyProperty="id"属性表示将自增后的主键放到传入
        //对象的id属性中保存
        specDao.insertSelective(spec.getSpecification());

        //2. 添加规格选项数据
        if(spec.getSpecificationOptionList() != null){
            for(SpecificationOption option :spec.getSpecificationOptionList()){
                option.setSpecId(spec.getSpecification().getId());
                optionDao.insertSelective(option);
            }
        }
    }

    @Override
    public SpecEntity findOne(Long id) {
        //1. 查询规格表数据
        Specification specification = specDao.selectByPrimaryKey(id);
        //2. 查询规格选项表数据
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<SpecificationOption> optionList = optionDao.selectByExample(query);

        SpecEntity entity = new SpecEntity();
        entity.setSpecification(specification);
        entity.setSpecificationOptionList(optionList);
        return entity;
    }

    @Override
    public void update(SpecEntity entity) {
        //1. 修改规格数据
        specDao.updateByPrimaryKeySelective(entity.getSpecification());

        //2. 根据规格id删除所有的规格选项数据
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(entity.getSpecification().getId());
        optionDao.deleteByExample(query);

        //3. 将新的规格选项集合数据添加到数据库中
        if(entity.getSpecificationOptionList() != null){
            for(SpecificationOption option : entity.getSpecificationOptionList()){
                option.setSpecId(entity.getSpecification().getId());
                optionDao.insertSelective(option);

            }
        }
    }

    @Override
    public void delete(Long[] ids) {
        if(ids != null){
            for(Long id : ids){
                //1. 根据规格id删除规格表数据
                specDao.deleteByPrimaryKey(id);
                //2. 根据规格id删除规格选项数据
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(id);
                optionDao.deleteByExample(query);
            }
        }
    }

    @Override
    public List<Map> selectOptionList() {
        List<Map> maps = specDao.selectOptionList();
        return maps;
    }
}
