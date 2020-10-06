package com.matis.objdetector.Controllers.RestControllers;

import com.matis.objdetector.Controllers.Helpers.*;
import com.matis.objdetector.Model.ChannelsLoader;
import com.matis.objdetector.Model.VideoChannel.Channel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("channels")
public class StreamsController {

    @GetMapping(value = "{chId}/streams/{streamId}/enable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getEnable(@PathVariable("chId") String chId,
                                              @PathVariable("streamId") int streamId) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(chId);
        if (ch != null) {
            String responseValue = Boolean.toString(ch.videoStreamList.get(streamId).enableStream.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResValue(false, "null"), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "{chId}/streams/{streamId}/enable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> setEnable(@RequestBody RecValue value,
                                              @PathVariable("chId") String chId,
                                              @PathVariable("streamId") int streamId) {
        try {
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).enableStream.set(Boolean.valueOf(value.getValue()));
            String responseValue = Boolean.toString(ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).enableStream.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } catch (ArrayIndexOutOfBoundsException ex) {
            String responseValue = Boolean.toString(ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).enableStream.get());
            return new ResponseEntity<>(new ResValue(false, responseValue), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "{chId}/streams/{streamId}/inputUrl", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getInputStream(@PathVariable("chId") String chId,
                                                   @PathVariable("streamId") int streamId) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(chId);
        if (ch != null) {
            String responseValue = ch.videoStreamList.get(streamId).inputUrl;
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "{chId}/streams/{streamId}/inputUrl", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> setInputStream(@RequestBody RecValue value,
                                                   @PathVariable("chId") String chId,
                                                   @PathVariable("streamId") int streamId) {
        try {
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).inputUrl = value.getValue();
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).refrashStream.set(true);
            String responseValue = ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).inputUrl;
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "{chId}/streams/{streamId}/queue1fps", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getQueue1Fps(@PathVariable("chId") String chId,
                                                 @PathVariable("streamId") int streamId) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(chId);
        if (ch != null) {
            String responseValue = Long.toString(ch.videoStreamList.get(streamId).grabberFpsQueue1.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "{chId}/streams/{streamId}/queue1fps", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> setQueue1Fps(@RequestBody RecValue value,
                                                 @PathVariable("chId") String chId,
                                                 @PathVariable("streamId") int streamId) {
        try {
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId - 1).grabberFpsQueue1.set(Integer.valueOf(value.getValue()));
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId - 1).refrashStream.set(true);
            String responseValue = Long.toString(ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId - 1).grabberFpsQueue1.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "{chId}/streams/{streamId}/queue2fps", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getQueue2Fps(@PathVariable("chId") String chId,
                                                 @PathVariable("streamId") int streamId) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(chId);
        if (ch != null) {
            String responseValue = Long.toString(ch.videoStreamList.get(streamId).grabberFpsQueue2.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "{chId}/streams/{streamId}/queue2fps", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> setQueue2Fps(@RequestBody RecValue value,
                                                 @PathVariable("chId") String chId,
                                                 @PathVariable("streamId") int streamId) {
        try {
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).grabberFpsQueue2.set(Integer.valueOf(value.getValue()));
            ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).refrashStream.set(true);
            String responseValue = Long.toString(ChannelsLoader.channelsContainer.channels.get(chId)
                    .videoStreamList.get(streamId).grabberFpsQueue2.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "{chId}/streams/{streamId}/stream_id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getStreamId(@PathVariable("chId") String chId,
                                                @PathVariable("streamId") int streamId) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(chId);
        if (ch != null) {
            String responseValue = Integer.toString(ch.videoStreamList.get(streamId).id);
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "{chId}/streams/{streamId}/running", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getStreamRunning(@PathVariable("chId") String chId,
                                                @PathVariable("streamId") int streamId) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(chId);
        if (ch != null) {
            String responseValue = Boolean.toString(ch.videoStreamList.get(streamId).running.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }
}
