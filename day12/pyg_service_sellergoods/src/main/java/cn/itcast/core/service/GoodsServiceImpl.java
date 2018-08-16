package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.entity.GoodsEntity;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao descDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private ItemCatDao itemCaoDao;

    @Autowired
    private SellerDao sellerDao;

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public PageResult findPage(Goods goods, Integer page, Integer rows) {
        GoodsQuery query = new GoodsQuery();
        if (goods != null) {
            GoodsQuery.Criteria criteria = query.createCriteria();
            //根据状态查询
            if(goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus())){
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            //根据名称查询
            if(goods.getGoodsName() != null && !"".equals(goods.getGoodsName())){
                criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            //根据用户查询
            if(goods.getSellerId() != null && !"".equals(goods.getSellerId()) && !"admin".equals(goods.getSellerId())){
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
        }
        PageHelper.startPage(page, rows);
        Page<Goods> pageList = (Page<Goods>) goodsDao.selectByExample(query);
        return new PageResult(pageList.getTotal(), pageList.getResult());
    }

    @Override
    public GoodsEntity findOne(Long id) {
        //1. 获取商品对象
        Goods goods = goodsDao.selectByPrimaryKey(id);
        //2. 获取商品详情对象
        GoodsDesc goodsDesc = descDao.selectByPrimaryKey(id);
        //3. 获取库存对象
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(query);

        GoodsEntity goodsEntity = new GoodsEntity();
        goodsEntity.setGoods(goods);
        goodsEntity.setGoodsDesc(goodsDesc);
        goodsEntity.setItemList(itemList);
        return goodsEntity;
    }

    @Override
    public void update(GoodsEntity goodsEntity) {
        //1. 修改商品数据
        goodsDao.updateByPrimaryKeySelective(goodsEntity.getGoods());
        //2. 修改商品详情数据
        descDao.updateByPrimaryKeySelective(goodsEntity.getGoodsDesc());
        //3. 根据商品ID删除库存数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsEntity.getGoods().getId());
        itemDao.deleteByExample(query);
        //4. 添加新的库存数据
        addItemList(goodsEntity);
    }

    @Override
    public void dele(Long id) {
        //逻辑删除商品表
        Goods goods = new Goods();
        goods.setId(id);
        goods.setIsDelete("1");
        goodsDao.updateByPrimaryKeySelective(goods);
    }

    @Override
    public void updateStatus(Long id, String status) {
        //1. 改变数据库中商品的审核状态
        Goods goods = new Goods();
        goods.setId(id);
        goods.setAuditStatus(status);
        goodsDao.updateByPrimaryKeySelective(goods);

        //2. 改变数据库中库存的审核状态
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdEqualTo(id);

        Item item = new Item();
        item.setStatus(status);
        itemDao.updateByExampleSelective(item, itemQuery);


    }


    @Override
    public void add(GoodsEntity goodsEntity) {
        //1. 添加商品对象
        //初始化商品状态为0, 未审核
        goodsEntity.getGoods().setAuditStatus("0");
        goodsDao.insertSelective(goodsEntity.getGoods());
        //2. 添加商品详情对象
        goodsEntity.getGoodsDesc().setGoodsId(goodsEntity.getGoods().getId());
        descDao.insertSelective(goodsEntity.getGoodsDesc());
        //3. 添加库存对象
        addItemList(goodsEntity);
    }

    /**
     * 添加库存数据
     * @param goodsEntity
     */
    public void addItemList(GoodsEntity goodsEntity) {
        if("1".equals(goodsEntity.getGoods().getIsEnableSpec())){
            for(Item item : goodsEntity.getItemList()){
                //获取页面传入的规格json字符串
                //{"机身内存":"16G","网络":"联通3G"}
                /**
                 * //将json数据转成list, 要求json字符串一定是[]数组形式
                 json.parseAarray(json字符串, 转换成的list的泛型的类型)
                 //将json数据转换成Java对象, 要求json字符串是{}, 直接是json形式
                 json.parseObject(json字符串, 转换成的对象的类型)
                 */
                Map<String, String> specMap = JSON.parseObject(item.getSpec(), Map.class);
                String title = goodsEntity.getGoods().getGoodsName();
                //在商品名称后拼接上规格, 为了全文检索, 查询精确
                if (specMap != null) {
                    for(Map.Entry<String, String> entry: specMap.entrySet()){
                        title += " " + entry.getValue();
                    }
                }
                //库存标题, 标题由商品名称 + 规格组成
                item.setTitle(title);
                //库存数据状态, 库存默认设置成0, 未审核状态
                item.setStatus("0");
                //设置库存对象的值
                item = setItemValue(goodsEntity, item);
                itemDao.insertSelective(item);
            }
        } else {
            //没有库存数据, 初始化一条数据, 要么库存数据为null会报错
            Item item = new Item();
            //初始化库存标题
            item.setTitle(goodsEntity.getGoods().getGoodsName());
            //初始化状态为1已审核
            item.setStatus("1");
            //初始化规格数据, 为一个空的json串
            item.setSpec("{}");
            //初始化售价
            item.setPrice(new BigDecimal("99999999999"));
            //初始化库存数量
            item.setNum(0);
            //设置库存对象的值
            item = setItemValue(goodsEntity, item);
            itemDao.insertSelective(item);

        }
    }


    /**
     * 设置库存对象属性值
     * @param goodsEntity  商品封装对象, 里面包含商品, 商品详情和库存基本数据
     * @param item        库存数据
     */
    public Item setItemValue(GoodsEntity goodsEntity, Item item) {

        //品牌名称
        Brand brand = brandDao.selectByPrimaryKey(goodsEntity.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //分类id
        item.setCategoryid(goodsEntity.getGoods().getCategory3Id());
        //分类名称
        ItemCat itemCat3 = itemCaoDao.selectByPrimaryKey(goodsEntity.getGoods().getCategory3Id());
        item.setCategory(itemCat3.getName());
        //商品id
        item.setGoodsId(goodsEntity.getGoods().getId());
        //商品图片, 默认取第一张图片作为样例图片
        String itemImages = goodsEntity.getGoodsDesc().getItemImages();
        List<Map> maps = JSON.parseArray(itemImages, Map.class);
        if(maps != null && maps.size() > 0){
            String url = (String)maps.get(0).get("url");
            item.setImage(url);
        }
        //商家id
        item.setSellerId(goodsEntity.getGoods().getSellerId());
        //商家名称
        Seller seller = sellerDao.selectByPrimaryKey(goodsEntity.getGoods().getSellerId());
        item.setSeller(seller.getName());


        //更新时间
        item.setUpdateTime(new Date());
        //创建时间
        item.setCreateTime(new Date());
        return item;
    }

}
