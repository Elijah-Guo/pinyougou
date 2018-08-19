package cn.itcast.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * spring Security给用户赋权限
 * 前提是这个用必须已经登录成功, 如果登录不成功进入不到这个方法中
 */
public class UserDetailServiceImpl implements UserDetailsService{
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.声明springSecurity权限集合
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
        //2. 向权限集合中添加权限
        authList.add(new SimpleGrantedAuthority("ROLE_USER"));
        //3. 返回用户对象, 第一个参数:是cas认证成功后返回的用户名, 第二个参数是密码:我们这里不需要返回空的字符串
        //第三个参数:给这个用户所赋予的权限集合
        return new User(username, "", authList);
    }
}
