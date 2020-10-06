package com.matis.objdetector.Controllers.RestControllers;

import com.matis.objdetector.Controllers.Exceptions.NotFoundException;
import com.matis.objdetector.Controllers.Helpers.*;
import com.matis.objdetector.Model.ChannelsLoader;
import com.matis.objdetector.Model.VideoChannel.Channel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("channels")
public class MotionDetectorController {

    @GetMapping(value = "{id}/mdetector/enable",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getEnable(@PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch != null) {
            String responseValue = Boolean.toString(ch.motionDetector.enableDetector.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } else {
            throw new NotFoundException();
        }

    }

    //fetch('/channels/8OhaeAVs/name'. { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({text: 'channnnneeeeelll'})}). then(console. log)
    @PutMapping(value = "{id}/mdetector/enable",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> setEnable(@RequestBody RecValue value, @PathVariable String id) {
        try {
            ChannelsLoader.channelsContainer.channels.get(id).motionDetector.enableDetector.set(Boolean.valueOf(value.getValue()));
            String responseValue = Boolean.toString(ChannelsLoader.channelsContainer.channels.get(id).motionDetector.enableDetector.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "{id}/mdetector/running",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getRunning(@PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch != null) {
            String responseValue = Boolean.toString(ch.motionDetector.running.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }



    @GetMapping(value = "{id}/mdetector/stream_sel",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getStreamSelector(@PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch != null) {
            String responseValue = Integer.toString(ch.motionDetector.streamSelector.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }

    //fetch('/channels/8OhaeAVs/name'. { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({text: 'channnnneeeeelll'})}). then(console. log)
    @PutMapping(value = "{id}/mdetector/stream_sel",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> setStreamSelector(@RequestBody RecValue value, @PathVariable String id) {
        try {
            ChannelsLoader.channelsContainer.channels.get(id).motionDetector.streamSelector.set(Integer.valueOf(value.getValue()));
            String responseValue = Integer.toString(ChannelsLoader.channelsContainer.channels.get(id).motionDetector.streamSelector.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "{id}/mdetector/threshold",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getThreshold(@PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch != null) {
            String responseValue = Integer.toString(ch.motionDetector.threshold.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }

    //fetch('/channels/8OhaeAVs/name'. { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({text: 'channnnneeeeelll'})}). then(console. log)
    @PutMapping(value = "{id}/mdetector/threshold",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> setThreshold(@RequestBody RecValue value, @PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        try {
            ChannelsLoader.channelsContainer.channels.get(id).motionDetector.threshold.set(Integer.valueOf(value.getValue()));
            String responseValue = Integer.toString(ChannelsLoader.channelsContainer.channels.get(id).motionDetector.threshold.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(new ResValue(true, ""), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "{id}/mdetector/zone",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getZone(@PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch != null) {
            String responseValue = ch.motionDetector.motionZone;
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResValue(true, ""), HttpStatus.NOT_FOUND);
        }
    }

    //fetch('/channels/8OhaeAVs/name'. { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({text: 'channnnneeeeelll'})}). then(console. log)
    @PutMapping(value = "{id}/mdetector/zone",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> setZone(@RequestBody RecValue value, @PathVariable String id) {
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        try {
            ChannelsLoader.channelsContainer.channels.get(id).motionDetector.motionZone=value.getValue();
            ChannelsLoader.channelsContainer.channels.get(id).motionDetector.refreshDetector.set(true);
            String responseValue = ChannelsLoader.channelsContainer.channels.get(id).motionDetector.motionZone;
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(new ResValue(true, ""), HttpStatus.NOT_FOUND);
        }
    }

}
