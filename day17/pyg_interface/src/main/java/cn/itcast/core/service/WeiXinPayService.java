package cn.itcast.core.service;

import cn.itcast.core.pojo.log.PayLog;

import java.util.Map;

public interface WeiXinPayService {

    /**
     * 生成微信支付二维码
     * @param out_trade_no 支付单号
     * @param total_fee 金额(分)
     * @return
     */
    public Map createNative(String out_trade_no, String total_fee);

    public Map queryPayStatus(String out_trade_no);

    /**
     * 根据用户查询payLog
     * @param userId
     * @return
     */
    public PayLog searchPayLogFromRedis(String userId);

    /**
     * 修改订单状态
     * @param out_trade_no 支付订单号
     * @param transaction_id 微信返回的交易流水号
     */
    public void updateOrderStatus(String out_trade_no,String transaction_id);



}
