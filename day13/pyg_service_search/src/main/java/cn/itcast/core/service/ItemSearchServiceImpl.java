package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {


    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map paramMap) {
        /**
         * 定义返回数据Map
         */
        Map<String, Object> resultMap = new HashMap<>();

        /**
         * 1. 高亮分页查询
         */
        resultMap.putAll(highlightQuery(paramMap));

        /**
         * 2. 查询分类
         */
        List<String> categoryList = searchCategoryList(paramMap);
        resultMap.put("categoryList", categoryList);

        /**
         * 3. 加载品牌和规格数据
         */
        if (categoryList != null ) {
            //默认加载第一个分类, 根据分类中文名获取模板id
            Long templateId = findTemplateIdByCategoryNameFromRedis(categoryList.get(0));
            //根据模板id查询对应的规格和品牌集合
            Map<String, Object> brandAndSpecMap = findBrandAndSpecByTemplateId(templateId);
            resultMap.putAll(brandAndSpecMap);
        }

        return resultMap;
    }


    /**
     * 高亮分页查询
     * @return
     */
    private  Map<String, Object> highlightQuery(Map paramMap) {
        //获取查询关键字
//        String keywords = (String)paramMap.get("keywords");
//        //创建查询对象
//        Query query = new SimpleQuery();
//        //创建查询条件
//        Criteria criteria = new Criteria("item_keywords").is(keywords);
//        query.addCriteria(criteria);
//        //查询并返回结果
//        ScoredPage<Item> items = solrTemplate.queryForPage(query, Item.class);


        Map<String, Object> resultMap = new HashMap<>();

        /**
         * 1. 获取查询参数
         */
        //获取查询关键字
        String keywords = (String)paramMap.get("keywords");
        if (keywords != null && !"".equals(keywords)) {
            keywords = keywords.replaceAll(" ", "");
        }
        //获取当前页
        Integer pageNo = null;
        String pageNoStr = String.valueOf(paramMap.get("pageNo"));
        if (pageNoStr != null && !"".equals(pageNoStr)) {
            pageNo = Integer.parseInt(pageNoStr);
        } else {
            pageNo = 1;
        }
        /**
         * 2. 计算分页
         */
        //获取每页查询多少条数据
        Integer pageSize = null;
        String pageSizeStr = String.valueOf(paramMap.get("pageSize"));
        if (pageSizeStr != null && !"".equals(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
        } else {
            pageSize = 40;
        }
        //计算从第几条开始查询
        Integer start = (pageNo - 1) * pageSize;

        //创建高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();

        /**
         * 3. 创建查询条件
         * is和contains区别:
         * is: 对搜索的内容关键字进行切分词, 使用切分词后的词汇在索引库中逐个对比查找
         * contains: 对搜索内容关键字不进行切分词, 认为整体就是一个词, 搜索的时候使用像数据库中like模糊查询的方式查找
         */
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);


        /**
         * 4. 设置高亮选项
         */
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置需要在哪个域中高亮显示
        highlightOptions.addField("item_title");
        //设置高亮的前缀
        highlightOptions.setSimplePrefix("<em style=\"color:red\">");
        //设置高亮的后缀
        highlightOptions.setSimplePostfix("</em>");
        //将高亮选项添加到查询对象中
        query.setHighlightOptions(highlightOptions);

        /**
         * 5. 设置分页
         */
        //设置从第几条开始查询
        query.setOffset(start);
        //设置每页查询多少条数据
        query.setRows(pageSize);


        /**
         * 设置过滤查询
         */
        /**
         * 根据分类过滤
         */
        String category = String.valueOf(paramMap.get("category"));
        if(category != null && !"".equals(category)){
            //设置根据分类过滤
            FilterQuery categoryQuery = new SimpleFilterQuery();
            Criteria categoryCriteria = new Criteria("item_category").is(category);
            //将过滤查询条件放入过滤对象中
            categoryQuery.addCriteria(categoryCriteria);
            //将过滤对象放入查询对象中
            query.addFilterQuery(categoryQuery);
        }

        /**
         * 根据品牌过滤
         */
        String brand = String.valueOf(paramMap.get("brand"));
        if(brand != null && !"".equals(brand)){
            //设置根据品牌过滤
            FilterQuery brandQuery = new SimpleFilterQuery();
            Criteria brandCriteria = new Criteria("item_brand").is(brand);
            //将过滤查询条件放入过滤对象中
            brandQuery.addCriteria(brandCriteria);
            //将过滤对象放入查询对象中
            query.addFilterQuery(brandQuery);
        }

        /**
         * 根据规格过滤
         */
        String specStr = String.valueOf(paramMap.get("spec"));
        Map<String, String> map = JSON.parseObject(specStr, Map.class);
        if(map != null){
            Set<String> set = map.keySet();
            for (String key : set) {
                //设置根据规格过滤
                FilterQuery specQuery = new SimpleFilterQuery();
                Criteria specCriteria = new Criteria("item_spec_" + key).is(map.get(key));
                //将过滤查询条件放入过滤对象中
                specQuery.addCriteria(specCriteria);
                //将过滤对象放入查询对象中
                query.addFilterQuery(specQuery);
            }
        }

        /**
         * 根据价格过滤
         *
         */
        //获取价格区间字符串
        String priceStr = String.valueOf(paramMap.get("price"));
        if(priceStr != null && !"".equals(priceStr)){
            FilterQuery priceQuery = new SimpleFilterQuery();
            //将价格区间字符串切割, 分割成最小值和最大值
            String[] price = priceStr.split("-");
            //大于等于最小值条件
            if(!"0".equals(price[0])){
                Criteria graterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                priceQuery.addCriteria(graterCriteria);
            }
            //小于等于最大值条件
            if(!"*".equals(price[1])){
                Criteria lessCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                priceQuery.addCriteria(lessCriteria);
            }
            query.addFilterQuery(priceQuery);
        }


        /**
         * 按照价格排序
         */
        //是升序还是降序
        String sort = String.valueOf(paramMap.get("sort"));
        //排序的域
        String sortField = String.valueOf(paramMap.get("sortField"));
        if(sortField != null && sort != null && !"".equals(sort) && !"".equals(sortField)){

            //升序排序
            if("ASC".equals(sort)){
                //创建排序对象
                Sort sortOption = new Sort(Sort.Direction.ASC, "item_" + sortField);
                //将排序对象加入到查询对象中
                query.addSort(sortOption);
            }
            //降序排序
            if ("DESC".equals(sort)) {
                Sort sortOption = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sortOption);
            }

        }


        //6. 返回高亮查询结果
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query, Item.class);
        //获取高亮结果集
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();
        //遍历结果集
        for(HighlightEntry<Item> entry : highlighted){
            //获取不带高亮的库存对象
            Item item = entry.getEntity();
            //获取高亮的库存标题
            if(entry.getHighlights() != null && entry.getHighlights().size() > 0){
                if(entry.getHighlights().get(0).getSnipplets() != null
                        && entry.getHighlights().get(0).getSnipplets().size() > 0){
                    String highlightTitle = entry.getHighlights().get(0).getSnipplets().get(0);
                    //给库存标题附上高亮的标题
                    item.setTitle(highlightTitle);
                }
            }
        }


        //查询到的结果集
        List<Item> content = items.getContent();
        //查询到的记录总数
        long totalElements = items.getTotalElements();

        resultMap.put("rows", content);
        resultMap.put("total", totalElements);
        resultMap.put("totalPages", items.getTotalPages());
        return resultMap;
    }

    /**
     * 根据关键字, 查询对应的分类名称集合
     * @param paramMap
     * @return
     */
    private  List<String> searchCategoryList(Map paramMap) {
        List<String> resultList = new ArrayList<>();
        //创建查询对象
        Query query = new SimpleQuery();
        //获取查询关键字
        String keywords = (String)paramMap.get("keywords");
        if (keywords != null && !"".equals(keywords)) {
            keywords = keywords.replaceAll(" ", "");
        }
        /**
         * 创建查询条件
         */
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);

        /**
         * 创建分组对象
         */
        GroupOptions groupOptions = new GroupOptions();
        //设置根据哪个域进行分组
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);


        //分组查询, 并返回结果, 分组的目的是为了对查询结果数据去重复
        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        //获取分组的域的结果集
        GroupResult<Item> item_category = items.getGroupResult("item_category");
        //提取分组对象
        Page<GroupEntry<Item>> groupEntries = item_category.getGroupEntries();
        //遍历, 提取分组后的结果字符串
        for (GroupEntry<Item> entry : groupEntries) {
            String groupValue = entry.getGroupValue();
            resultList.add(groupValue);
        }
        return resultList;
    }

    /**
     * 根据分类名称查询模板id
     * @param categoryName  分类名称
     * @return
     */
    private Long findTemplateIdByCategoryNameFromRedis(String categoryName) {
        Long templateId = (Long)redisTemplate.boundHashOps(Constants.CATEGORY_REDIS).get(categoryName);
        return templateId;
    }

    /**
     * 根据模板id, 获取品牌集合, 获取规格数据集合
     * @param templateId    模板id
     * @return
     */
    private Map<String, Object> findBrandAndSpecByTemplateId(Long templateId) {
        Map<String, Object> resultMap = new HashMap<>();
        /**
         * 获取品牌数据集合
         */
        List<Map> brandList = (List<Map>)redisTemplate.boundHashOps(Constants.BRAND_REDIS).get(templateId);
        resultMap.put("brandList", brandList);

        /**
         * 获取规格数据集合
         */
        List<Map> specList = (List<Map>)redisTemplate.boundHashOps(Constants.SPEC_REDIS).get(templateId);
        resultMap.put("specList", specList);
        return resultMap;
    }


}
