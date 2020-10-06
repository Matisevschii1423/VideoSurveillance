package com.matis.objdetector.Controllers.RestControllers;

import com.matis.objdetector.Controllers.Helpers.ResValue;
import com.matis.objdetector.Controllers.Helpers.RecValue;
import com.matis.objdetector.Model.ChannelsLoader;
import com.matis.objdetector.Model.VideoChannel.Channel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Set;

@RestController
@RequestMapping("channels")
public class ChannelsController {
    @GetMapping
    public ResponseEntity<ResValue> allChannels(){
        Set<String> keys = ChannelsLoader.channelsContainer.channels.keySet();
        HashMap<String,String> channelsName = new HashMap<>();
        for (String key:keys){
            channelsName.put(key,ChannelsLoader.channelsContainer.channels.get(key).name);
        }
        if (channelsName.size()!=0){
            String responseValue = channelsName.toString();
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "{id}/enable",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getEnable(@PathVariable String id){
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch!= null){
            String responseValue = Boolean.toString(ch.enableChannel.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }
    //fetch('/channels/8OhaeAVs/name'. { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({text: 'channnnneeeeelll'})}). then(console. log)
    @PutMapping(value = "{id}/enable",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> setEnable(@RequestBody RecValue value, @PathVariable String id){
        System.out.println("Ch-"+id+"-"+value.getValue());
        try{
            ChannelsLoader.channelsContainer.channels.get(id).enableChannel.set(Boolean.valueOf(value.getValue()));
            String responseValue = Boolean.toString(ChannelsLoader.channelsContainer.channels.get(id).enableChannel.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        }catch (NullPointerException ex){
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "{id}/name",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getName(@PathVariable String id){
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch!= null){
            String responseValue = ch.name;
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }
    //fetch('/channels/8OhaeAVs/name'. { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({text: 'channnnneeeeelll'})}). then(console. log)
    @PutMapping(value = "{id}/name",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> setName(@RequestBody RecValue value, @PathVariable String id){
        try{
            ChannelsLoader.channelsContainer.channels.get(id).name=value.getValue();
            String responseValue = ChannelsLoader.channelsContainer.channels.get(id).name;
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        }catch (NullPointerException ex){
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.OK);
        }
    }

    @GetMapping(value = "{id}/number",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResValue> getNumber(@PathVariable String id){
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch!= null){
            String responseValue = Integer.toString(ch.number.get());
            return new ResponseEntity<>(new ResValue(true, responseValue), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ResValue(false, ""), HttpStatus.NOT_FOUND);
        }
    }



}
