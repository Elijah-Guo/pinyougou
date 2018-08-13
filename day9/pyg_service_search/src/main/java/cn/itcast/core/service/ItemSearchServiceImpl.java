package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {


    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map paramMap) {
        Map<String, Object> resultMap = new HashMap<>();

        //获取查询关键字
        String keywords = (String)paramMap.get("keywords");
        //创建查询对象
        Query query = new SimpleQuery();
        //创建查询条件
        Criteria criteria = new Criteria("item_keywords").contains(keywords);
        query.addCriteria(criteria);
        //查询并返回结果
        ScoredPage<Item> items = solrTemplate.queryForPage(query, Item.class);
        //查询到的结果集
        List<Item> content = items.getContent();
        //查询到的记录总数
        long totalElements = items.getTotalElements();

        resultMap.put("rows", content);
        resultMap.put("total", totalElements);
        resultMap.put("totalPages", items.getTotalPages());
        return resultMap;
    }
}
