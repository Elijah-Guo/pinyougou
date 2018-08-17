package cn.itcast.core.service;

public interface ItemManagerService {

    public void itemToSolr(Long goodsId);

    public void delItemFromSolr(Long goodsId);
}
