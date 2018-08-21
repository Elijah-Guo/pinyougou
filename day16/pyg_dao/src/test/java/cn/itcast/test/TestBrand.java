package cn.itcast.test;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-dao.xml"})
public class TestBrand {

    @Autowired
    private BrandDao brandDao;

    @Test
    public void testFindBrandById() {
        Brand brand = brandDao.selectByPrimaryKey(1L);
        System.out.println(brand);

    }

    @Test
    public void testFindBrandByQuery() {
        //查询所有
       // List<Brand> brands = brandDao.selectByExample(null);

        //创建查询条件对象
        BrandQuery brandQuery = new BrandQuery();
        //查询指定字段
        brandQuery.setFields("id,name");
        //按照id降序
        brandQuery.setOrderByClause("id desc");

        //创建where条件查询对象
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        //根据品牌名称模糊查询
        criteria.andNameLike("%联%");
        List<Brand> brands = brandDao.selectByExample(brandQuery);

        System.out.println(brands);

    }
}
