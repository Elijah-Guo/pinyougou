package cn.itcast.core.service;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl implements  UserService{

    @Autowired
    private UserDao userDao;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ActiveMQQueue smsDestination;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${template_code}")
    private String template_code;

    @Value("${sign_name}")
    private String sign_name;

    @Override
    public void sendCode(final String phone) {

        //1. 生成随机6为数作为验证码
        final long smscode = (long)(Math.random() * 1000000);

        //2. 将手机号作为key, 验证码作为value存入redis服务器
        redisTemplate.boundHashOps(Constants.SMS_REDIS).put(phone, smscode);

        //3. 将短信内容, 手机号, 模板编号, 签名, 发送给消息服务器
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage message = session.createMapMessage();
                message.setString("mobile", phone);//手机号
                message.setString("template_code", template_code);//模板编码
                message.setString("sign_name", sign_name);//签名
                Map map=new HashMap();
                map.put("code", smscode);	//验证码
                message.setString("param", JSON.toJSONString(map));
                return (Message) message;

            }
        });
    }

    @Override
    public void add(User user) {
        userDao.insertSelective(user);
    }

    @Override
    public Boolean checkSmsCode(String phone, String smsCode) {
        //从redis中获取我们自己存入的短信验证码
        long redisCode = (long) redisTemplate.boundHashOps(Constants.SMS_REDIS).get(phone);
        if(redisCode == 0){
            return false;
        }
        //判断验证码是否正确, 不正确返回false
        if(!smsCode.equals(String.valueOf(redisCode))){
            return false;
        }
        return true;
    }


    //测试生成6为随机数字
//    public static void main(String[] args) throws Exception {
//        long i = (long)(Math.random() * 1000000);
//        System.out.println("======" + i);
//    }
}
