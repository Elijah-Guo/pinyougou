package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;

    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<ItemCat> itemCats = itemCatDao.selectByExample(query);
        return itemCats;
    }

    @Override
    public void add(ItemCat itemCat) {
        if(itemCat.getParentId() == null){

        }
        itemCatDao.insertSelective(itemCat);
    }

    @Override
    public ItemCat findOne(Long id) {
        ItemCat itemCat = itemCatDao.selectByPrimaryKey(id);
        return itemCat;
    }

    @Override
    public List<ItemCat> findAll() {
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        return itemCats;
    }
}
