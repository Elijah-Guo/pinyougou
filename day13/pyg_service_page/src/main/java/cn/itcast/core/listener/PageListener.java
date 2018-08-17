package cn.itcast.core.listener;

import cn.itcast.core.service.CmsService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Map;

/**
 * 自定义监听器, 监听来自于ActiveMq消息服务器发送过来的消息, 商品ID,
 * 我们根据商品ID, 到数据库获取商品的详细数据, 生成静态化页面
 */
public class PageListener implements MessageListener{

    @Autowired
    private CmsService cmsService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;
        try {
            //获取商品id
            String goodsId = atm.getText();
            //将根据商品详细数据生成商品详情的静态化页面
            Map<String, Object> rootMap = cmsService.findGoods(Long.parseLong(goodsId));
            cmsService.createStaticPage(Long.parseLong(goodsId), rootMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
