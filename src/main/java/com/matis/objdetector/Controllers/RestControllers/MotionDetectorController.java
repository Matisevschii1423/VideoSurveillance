package com.matis.objdetector.Controllers.RestControllers;

import com.matis.objdetector.Controllers.Exceptions.NotFoundException;
import com.matis.objdetector.Controllers.Helpers.ReqBoolValue;
import com.matis.objdetector.Controllers.Helpers.ReqIntValue;
import com.matis.objdetector.Controllers.Helpers.ReqStringValue;
import com.matis.objdetector.Controllers.Helpers.Succes;
import com.matis.objdetector.Model.ChannelsLoader;
import com.matis.objdetector.Model.VideoChannel.Channel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("channels")
public class MotionDetectorController {

    @GetMapping("{id}/mdetector/enable")
    public boolean getEnable(@PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch != null) {
            return ch.motionDetector.enableDetector.get();
        } else {
            throw new NotFoundException();
        }

    }

    //fetch('/channels/8OhaeAVs/name'. { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({text: 'channnnneeeeelll'})}). then(console. log)
    @PutMapping("{id}/mdetector/enable")
    public ResponseEntity<Succes> setEnable(@RequestBody ReqBoolValue value, @PathVariable String id) {
        System.out.println(id +"----"+value.getValue());
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        try {
            ChannelsLoader.channelsContainer.channels.get(id).motionDetector.enableDetector.set(value.getValue());
            return new ResponseEntity<>(new Succes(true), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(new Succes(false), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{id}/mdetector/running")
    public boolean getRunning(@PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch != null) {
            return ch.motionDetector.running.get();
        } else {
            throw new NotFoundException();
        }
    }



    @GetMapping("{id}/mdetector/stream_sel")
    public Integer getStreamSelector(@PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch != null) {
            return ch.motionDetector.streamSelector.get();
        } else {
            throw new NotFoundException();
        }
    }

    //fetch('/channels/8OhaeAVs/name'. { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({text: 'channnnneeeeelll'})}). then(console. log)
    @PutMapping("{id}/mdetector/stream_sel")
    public ResponseEntity<Succes> setStreamSelector(@RequestBody ReqIntValue value, @PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        try {
            ChannelsLoader.channelsContainer.channels.get(id).motionDetector.streamSelector.set(value.getValue());
            return new ResponseEntity<>(new Succes(true), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(new Succes(false), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{id}/mdetector/threshold")
    public Integer getThreshold(@PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch != null) {
            return ch.motionDetector.threshold.get();
        } else {
            throw new NotFoundException();
        }
    }

    //fetch('/channels/8OhaeAVs/name'. { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({text: 'channnnneeeeelll'})}). then(console. log)
    @PutMapping("{id}/mdetector/threshold")
    public ResponseEntity<Succes> setThreshold(@RequestBody ReqIntValue value, @PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        try {
            ChannelsLoader.channelsContainer.channels.get(id).motionDetector.threshold.set(value.getValue());
            return new ResponseEntity<>(new Succes(true), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(new Succes(false), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{id}/mdetector/zone")
    public String getZone(@PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch != null) {
            return ch.motionDetector.motionZone;
        } else {
            throw new NotFoundException();
        }
    }

    //fetch('/channels/8OhaeAVs/name'. { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({text: 'channnnneeeeelll'})}). then(console. log)
    @PutMapping("{id}/mdetector/zone")
    public ResponseEntity<Succes> setZone(@RequestBody ReqStringValue value, @PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        try {
            ChannelsLoader.channelsContainer.channels.get(id).motionDetector.motionZone=value.getValue();
            ChannelsLoader.channelsContainer.channels.get(id).motionDetector.refreshDetector.set(true);
            return new ResponseEntity<>(new Succes(true), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(new Succes(false), HttpStatus.NOT_FOUND);
        }
    }

}
