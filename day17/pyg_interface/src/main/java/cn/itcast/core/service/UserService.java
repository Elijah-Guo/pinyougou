package cn.itcast.core.service;

import cn.itcast.core.pojo.user.User;

public interface UserService {

    public void sendCode(final String phone);

    public void add(User user);

    public Boolean checkSmsCode(String phone, String smsCode);
}
