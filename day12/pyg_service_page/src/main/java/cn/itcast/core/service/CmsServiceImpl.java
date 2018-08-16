package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CmsServiceImpl implements CmsService , ServletContextAware{

    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    private ServletContext servletContext;

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private ItemCatDao itemCatDao;


    @Override
    public void createStaticPage(Long goodsId, Map<String, Object> rootMap) throws Exception {
        //获得freemarker初始化对象
        Configuration configuration = freemarkerConfig.getConfiguration();
        //加载模板对象
        Template template = configuration.getTemplate("item.ftl");

        //拼接并获取当前文件的文件名和路径
        String path = goodsId + ".html";
        //绝对路径
        String realPath = getRealPath(path);

        //将当前路径转换成file对象
        File file = new File(realPath);
        //获取当前的父级目录
       // File parentFile = file.getParentFile();
        //判断父级目录是否存在
//        if(!parentFile.exists()){
//            //如果父级目录不存在则自动创建
//            parentFile.mkdir();
//        }

        //创建输出流, 指定文件生成的路径和生成的文件名
        Writer out = new FileWriterWithEncoding(file,"utf-8");
        //生成静态化页面
        template.process(rootMap, out);
    }

    /**
     * 获取当前项目的绝对路径, 并且拼接生成的静态文件的文件名
     * @param path   静态文件文件名
     * @return
     */
    private String getRealPath(String path) {
        String realPath = servletContext.getRealPath(path);
        return realPath;
    }

    /**
     * spring 传入servletContext对象, 我们用这个对象给我们当前类的servletContext对象赋值, 初始化
     * @param servletContext
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public Map<String, Object> findGoods(Long goodsId) {
        Map<String, Object> resultMap = new HashMap<>();

        //1. 根据商品ID获取商品数据
        Goods goods = goodsDao.selectByPrimaryKey(goodsId);
        resultMap.put("goods", goods);

        //2. 根据商品id获取商品详情数据
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(goodsId);
        resultMap.put("goodsDesc", goodsDesc);

        //3. 根据商品id获取库存数据
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        resultMap.put("itemList", itemList);

        //4. 根据商品id获取分类对象数据
        if (goods != null) {
            ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());

            resultMap.put("itemCat1", itemCat1);
            resultMap.put("itemCat2", itemCat2);
            resultMap.put("itemCat3", itemCat3);
        }


        return resultMap;
    }
}
