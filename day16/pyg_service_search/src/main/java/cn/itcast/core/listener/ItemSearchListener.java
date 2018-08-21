package cn.itcast.core.listener;

import cn.itcast.core.service.ItemManagerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 自定消息监听器,  监听来自于activeMq消息服务器发送过来的商品id
 * 根据商品ID, 到数据库中获取商品的详细数据, 然后将详细数据放入solr索引库中供前台系统搜索使用
 */
public class ItemSearchListener implements MessageListener{

    @Autowired
    private ItemManagerService itemManagerService;

    @Override
    public void onMessage(Message message) {
        //将JDK原生的消息对象强转成activeMq的消息对象, 为了方便提取数据处理
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;

        try {
            //获取文本消息, 商品ID
            String goodsId = atm.getText();
            //将商品放入solr索引库中供前台系统搜索
            itemManagerService.itemToSolr(Long.parseLong(goodsId));
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
