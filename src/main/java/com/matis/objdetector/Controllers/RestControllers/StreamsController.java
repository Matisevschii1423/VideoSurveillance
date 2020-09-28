package com.matis.objdetector.Controllers.RestControllers;


import com.matis.objdetector.Controllers.Exceptions.NotFoundException;
import com.matis.objdetector.Controllers.Helpers.ReqBoolValue;
import com.matis.objdetector.Controllers.Helpers.ReqIntValue;
import com.matis.objdetector.Controllers.Helpers.ReqStringValue;
import com.matis.objdetector.Model.ChannelsLoader;
import com.matis.objdetector.Model.VideoChannel.Channel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("channels")
public class StreamsController {

    @GetMapping("{chId}/streams/{streamId}/enable")
    public boolean getEnable(@PathVariable("chId") String chId,@PathVariable("streamId") int streamId ){
        Channel ch = ChannelsLoader.channelsContainer.channels.get(chId);
        if (ch!=null){
            return ch.videoStreamList.get(streamId).enableStream.get();
        }else{
            throw new NotFoundException();
        }
    }

    @PutMapping("{chId}/streams/{streamId}/enable")
    public void setEnable(@RequestBody ReqBoolValue value, @PathVariable("chId") String chId, @PathVariable("streamId") int streamId ){
        try {
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).enableStream.set(value.getValue());
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new NotFoundException();
        }
    }

    @GetMapping("{chId}/streams/{streamId}/inputUrl")
    public String getInputStream(@PathVariable("chId") String chId,@PathVariable("streamId") int streamId ){
        Channel ch = ChannelsLoader.channelsContainer.channels.get(chId);
        if (ch!=null){
            return ch.videoStreamList.get(streamId).inputUrl;
        }else{
            throw new NotFoundException();
        }
    }

    @PutMapping("{chId}/streams/{streamId}/inputUrl")
    public void setInputStream(@RequestBody ReqStringValue value, @PathVariable("chId") String chId, @PathVariable("streamId") int streamId ){
        try {
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).inputUrl=value.getValue();
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).refrashStream.set(true);
        } catch (NullPointerException ex) {
            throw new NotFoundException();
        }
    }

    @GetMapping("{chId}/streams/{streamId}/queue1fps")
    public int getQueue1Fps(@PathVariable("chId") String chId,@PathVariable("streamId") int streamId ){
        Channel ch = ChannelsLoader.channelsContainer.channels.get(chId);
        if (ch!=null){
            return (int)ch.videoStreamList.get(streamId).grabberFpsQueue1.get();
        }else{
            throw new NotFoundException();
        }
    }

    @PutMapping("{chId}/streams/{streamId}/queue1fps")
    public void setQueue1Fps(@RequestBody ReqIntValue value, @PathVariable("chId") String chId, @PathVariable("streamId") int streamId ){
        try {
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId-1).grabberFpsQueue1.set(value.getValue());
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId-1).refrashStream.set(true);
        } catch (NullPointerException ex) {
            throw new NotFoundException();
        }
    }

    @GetMapping("{chId}/streams/{streamId}/queue2fps")
    public int getQueue2Fps(@PathVariable("chId") String chId,@PathVariable("streamId") int streamId ){
        Channel ch = ChannelsLoader.channelsContainer.channels.get(chId);
        if (ch!=null){
            return (int)ch.videoStreamList.get(streamId).grabberFpsQueue2.get();
        }else{
            throw new NotFoundException();
        }
    }

    @PutMapping("{chId}/streams/{streamId}/queue2fps")
    public void setQueue2Fps(@RequestBody ReqIntValue value, @PathVariable("chId") String chId,@PathVariable("streamId") int streamId ){
        try {
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).grabberFpsQueue2.set(value.getValue());
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).refrashStream.set(true);
        } catch (NullPointerException ex) {
            throw new NotFoundException();
        }
    }

    @GetMapping("{chId}/streams/{streamId}/stream_id")
    public int getStreamId(@PathVariable("chId") String chId,@PathVariable("streamId") int streamId ){
        Channel ch = ChannelsLoader.channelsContainer.channels.get(chId);
        if (ch!=null){
            return ch.videoStreamList.get(streamId).id;
        }else{
            throw new NotFoundException();
        }
    }
}
