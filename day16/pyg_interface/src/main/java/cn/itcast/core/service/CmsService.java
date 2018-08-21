package cn.itcast.core.service;

import java.util.Map;

public interface CmsService {

    /**
     * 生成静态化页面方法
     * @param goodsId   商品id
     * @param rootMap   传入模板中的数据
     * @throws Exception
     */
    public void createStaticPage(Long goodsId, Map<String, Object> rootMap) throws Exception;

    /**
     * 根据商品ID获取商品详细数据, 包括商品, 商品详情, 库存, 分类等数据
     * @param goodsId
     * @return
     */
    public Map<String, Object> findGoods(Long goodsId);
}
