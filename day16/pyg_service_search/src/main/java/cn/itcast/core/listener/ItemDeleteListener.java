package cn.itcast.core.listener;

import cn.itcast.core.service.ItemManagerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 自定义监听器, 监听来自于activeMq消息服务器发送过来的消息商品ID,
 * 根据商品ID, 到solr索引库中删除对应的数据
 */
public class ItemDeleteListener implements MessageListener{

    @Autowired
    private ItemManagerService itemManagerService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;
        try {
            String goodsId = atm.getText();
            //根据商品id删除solr索引库对应的数据
            itemManagerService.delItemFromSolr(Long.parseLong(goodsId));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
