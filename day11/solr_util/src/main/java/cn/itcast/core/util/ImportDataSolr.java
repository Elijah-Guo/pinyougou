package cn.itcast.core.util;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ImportDataSolr {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private SolrTemplate solrTemplate;


    public void importDBDataToSolr () {
        //1. 调用库存dao, 查询库存表全部数据
        List<Item> items = itemDao.selectByExample(null);
        if (items != null) {
            for(Item item : items){
                //将获取的json格式字符串转换成map对象
                Map specMap = JSON.parseObject(item.getSpec(), Map.class);
                //给规格map赋值
                item.setSpecMap(specMap);
            }
        }
        //2. 将库存数据导入到solr索引库中
        solrTemplate.saveBeans(items);
        //提交
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        //获取spring管理对象
        //classpath加载当前所在项目的配置文件; classpath*, 加载当前项目和当前项目所依赖的项目下的配置文件
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        //获取当前类对象
        ImportDataSolr importUtil = (ImportDataSolr)applicationContext.getBean("importDataSolr");
        importUtil.importDBDataToSolr();
    }
}
