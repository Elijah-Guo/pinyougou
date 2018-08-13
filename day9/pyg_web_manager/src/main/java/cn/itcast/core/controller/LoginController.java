package cn.itcast.core.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/showName")
    public Map<String,String> showName() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        Map<String, String> map = new HashMap<>();
        map.put("userName", userName);
        return  map;
    }
}
