package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.GoodsEntity;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;

public interface GoodsService {

    public void add(GoodsEntity goodsEntity);

    public PageResult findPage(Goods goods, Integer page, Integer rows);

    public GoodsEntity findOne(Long id);

    public void update(GoodsEntity goodsEntity);

    public void dele(final Long id);

    public void updateStatus(final Long id, String status);
}
