package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;



    /**
     * 获取短信验证码
     * @param phone  手机号
     * @return
     */
    @RequestMapping("/sendCode")
    public Result sendCode(String phone){
        try {
            userService.sendCode(phone);
            return new Result(true,"验证码发送成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"验证码发送失败!");
        }

    }

    /**
     * 用户注册
     * @param smscode   页面上填写的验证码
     * @param user      用户实体对象
     * @return
     */
    @RequestMapping("/add")
    public Result add(String smscode, @RequestBody User user) {
        try {
            //判断验证码是否正确
            Boolean isCheck = userService.checkSmsCode(user.getPhone(), smscode);
            //如果验证码正确则注册
            if(isCheck){
                user.setCreated(new Date());
                user.setUpdated(new Date());
                user.setStatus("Y");
                user.setSourceType("1");
                userService.add(user);
                return  new Result(true, "注册成功!");
            } else {
                return new Result(false, "您的验证码输入错误, 请重新输入!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "注册失败!");
        }

    }
}
