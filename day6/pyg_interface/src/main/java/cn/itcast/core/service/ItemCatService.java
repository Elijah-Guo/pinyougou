package cn.itcast.core.service;

import cn.itcast.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {

    public List<ItemCat> findByParentId(Long parentId);

    public void add(ItemCat itemCat);

    public ItemCat findOne(Long id);
}
