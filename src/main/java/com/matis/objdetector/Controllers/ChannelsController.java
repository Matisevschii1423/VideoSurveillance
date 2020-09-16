package com.matis.objdetector.Controllers;

import com.matis.objdetector.Model.ChannelsLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ChannelsController {
    @GetMapping("/channels/view")
    public ModelAndView showStreams(){
        ModelAndView model = new ModelAndView("streams");

        //model.addObject("channelsForm", ChannelsLoader.getChannelsContainer().getAllChannels());
        return model;
    }
}
