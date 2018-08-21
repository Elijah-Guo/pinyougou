package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;

import java.util.List;
import java.util.Map;

@Service
public class ItemManagerServiceImpl implements  ItemManagerService{

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemDao itemDao;

    @Override
    public void itemToSolr(Long goodsId) {

        /**
         *  审核通过的时候, 根据商品ID到数据库中查询商品详细数据,
         *    将商品详细数据放入solr索引库中, 供前台系统搜索使用
         */

        //a. 调用库存dao, 查询库存表对应商品id的数据
        ItemQuery importDataItemQuery = new ItemQuery();
        ItemQuery.Criteria criteria1 = importDataItemQuery.createCriteria();
        criteria1.andGoodsIdEqualTo(goodsId);
        List<Item> items = itemDao.selectByExample(importDataItemQuery);
        if (items != null) {
            for(Item importDataItem : items){
                //将获取的json格式字符串转换成map对象
                Map specMap = JSON.parseObject(importDataItem.getSpec(), Map.class);
                //给规格map赋值
                importDataItem.setSpecMap(specMap);
            }
        }
        //b. 将库存数据导入到solr索引库中
        solrTemplate.saveBeans(items);
        //c. 提交
        solrTemplate.commit();

    }

    @Override
    public void delItemFromSolr(Long goodsId) {
        /**
         * 商品删除, 根据商品id删除solr索引库中对应的数据
         */
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").is(goodsId);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
