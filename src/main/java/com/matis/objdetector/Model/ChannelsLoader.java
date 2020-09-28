package com.matis.objdetector.Model;

import com.matis.objdetector.Model.VideoChannel.ChannelsContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ChannelsLoader {

    private Logger logger = LoggerFactory.getLogger(ChannelsLoader.class);

    public static ChannelsContainer channelsContainer;

    public ChannelsLoader() {
        channelsContainer = new ChannelsContainer();

    }

    @EventListener(ApplicationReadyEvent.class)
    public  void runChannels() {
        this.channelsContainer.serializationFile = "channels_config.bin";
        this.channelsContainer.restoreParameters();
        this.channelsContainer.executeAllChannels();
        this.channelsContainer.saveChannelsParameters();
    }
}
