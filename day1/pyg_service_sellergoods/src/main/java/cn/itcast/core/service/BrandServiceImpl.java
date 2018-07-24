package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
