package com.matis.objdetector.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Channels {
    @RequestMapping(value = "/channels")
    public String showView(){
        return "channels";
    }
    @PostMapping(value = "/channels")
    public String index(){
        return "channels";
    }
}
