package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService{

    @Autowired
    private BrandDao brandDao;


    @Override
    public List<Brand> findAll() {
        List<Brand> brands = brandDao.selectByExample(null);
        return brands;
    }

    @Override
    public PageResult findPage(Brand brand, Integer page, Integer rows) {
        //创建查询条件对象
        BrandQuery brandQuery = new BrandQuery();
        if(brand != null){
            //创建where条件对象
            BrandQuery.Criteria criteria = brandQuery.createCriteria();
            //拼接名称模糊查询条件
            if(brand.getName() != null && !"".equals(brand.getName())){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            //拼接根据首字母查询
            if(brand.getFirstChar() != null && !"".equals(brand.getFirstChar())){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        //告诉分页插件当前页, 以及每页需要展示多少条数据
        PageHelper.startPage(page, rows);
        //将数据库中查询出来的当前表的所有数据返回给分页插件
        Page<Brand> pageList = (Page<Brand>)brandDao.selectByExample(brandQuery);
        //从分页插件中获取当前页需要展示的数据
        return new PageResult(pageList.getTotal(), pageList.getResult());
    }

    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }

    @Override
    public Brand findOne(Long id) {
        Brand brand = brandDao.selectByPrimaryKey(id);
        return brand;
    }

    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Long[] ids) {
        if(ids != null){
            for(Long id : ids){
                brandDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public List<Map> selectOptionList() {
        List<Map> maps = brandDao.selectOptionList();
        return maps;
    }

}
