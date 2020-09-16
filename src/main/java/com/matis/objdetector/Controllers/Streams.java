package com.matis.objdetector.Controllers;

import com.matis.objdetector.Model.ChannelsLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Streams {
/*
    @GetMapping("/streams")
    public ModelAndView showStreams(){
        ModelAndView model = new ModelAndView("streams");
        model.addObject("channelsForm",ChannelsLoader.getChannelsContainer().getAllChannels());
        return model;
    }

    @PostMapping("/streams")
    public ModelAndView addStreams(
            @RequestParam("name") String name,
            @RequestParam(required=false,name="mainStream") String mainStream,
            @RequestParam(required=false,name="subStream")String subStream,
            @RequestParam(required=false,name="mainStreamEnable")boolean mainStreamEnable,
            @RequestParam(required=false,name="subStreamEnable")boolean substreamEnable){
        ChannelsLoader.addNewChannel(name,mainStream,subStream,mainStreamEnable,substreamEnable);

        return new ModelAndView("redirect:/streams");
    }


    @PostMapping("/streams/{nr}")
    public ModelAndView modifyStreams(
            @PathVariable int nr,
            @RequestParam("name") String name,
            @RequestParam("mainStream") String mainStream,
            @RequestParam("subStream")String subStream,
            @RequestParam(required=false,name="mainStreamEnable")boolean mainStreamEnable,
            @RequestParam(required=false,name="subStreamEnable")boolean substreamEnable){

        ChannelsLoader.getChannelsContainer().getChannelDataByNr(nr).getData().setName(name);
        ChannelsLoader.getChannelsContainer().getChannelDataByNr(nr).getMainStream().data.setInput(mainStream);
        ChannelsLoader.getChannelsContainer().getChannelDataByNr(nr).getSubStream().data.setInput(subStream);
        ChannelsLoader.getChannelsContainer().getChannelDataByNr(nr).getMainStream().data.setEnable(mainStreamEnable);
        ChannelsLoader.getChannelsContainer().getChannelDataByNr(nr).getSubStream().data.setEnable(substreamEnable);


        System.out.println(nr+"--"+name+"--"+mainStream+"--"+subStream+"--"+mainStreamEnable+"--"+substreamEnable);
        return new ModelAndView("redirect:/streams");
    }





     */


}
