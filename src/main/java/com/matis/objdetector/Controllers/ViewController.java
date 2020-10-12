package com.matis.objdetector.Controllers;

import com.matis.objdetector.Model.ChannelsLoader;
import com.matis.objdetector.Model.VideoChannel.Channel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class ViewController {
    @RequestMapping(value = "/main_view")
    public ModelAndView mainTemplate(){

        ModelAndView model = new ModelAndView("main_view");
        Set<String> keys = ChannelsLoader.channelsContainer.channels.keySet();
        List<Channel> channels = new ArrayList<>();
        for(String key: keys){
            channels.add(ChannelsLoader.channelsContainer.channels.get(key));
        }
        model.addObject("channels", channels);
        return model;
    }

    @RequestMapping(value = "/motion_det")
    public ModelAndView motionDet(){

        ModelAndView model = new ModelAndView("motion_det");
        Set<String> keys = ChannelsLoader.channelsContainer.channels.keySet();
        List<Channel> channels = new ArrayList<>();
        for(String key: keys){
            channels.add(ChannelsLoader.channelsContainer.channels.get(key));
        }
        model.addObject("channels", channels);
        return model;
    }
    @RequestMapping(value = "/obj_det")
    public ModelAndView objDet(){

        ModelAndView model = new ModelAndView("obj_det");
        Set<String> keys = ChannelsLoader.channelsContainer.channels.keySet();
        List<Channel> channels = new ArrayList<>();
        for(String key: keys){
            channels.add(ChannelsLoader.channelsContainer.channels.get(key));
        }
        model.addObject("channels", channels);
        return model;
    }
    @RequestMapping(value = "/ch_param")
    public ModelAndView chParam(){

        ModelAndView model = new ModelAndView("ch_param");
        Set<String> keys = ChannelsLoader.channelsContainer.channels.keySet();
        List<Channel> channels = new ArrayList<>();
        for(String key: keys){
            channels.add(ChannelsLoader.channelsContainer.channels.get(key));
        }
        model.addObject("channels", channels);
        return model;
    }
}
