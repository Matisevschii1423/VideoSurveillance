package com.matis.objdetector.Controllers.RestControllers;

import com.matis.objdetector.Controllers.Exceptions.NotFoundException;
import com.matis.objdetector.Controllers.Helpers.ReqBoolValue;
import com.matis.objdetector.Controllers.Helpers.ReqStringValue;
import com.matis.objdetector.Controllers.Helpers.Succes;
import com.matis.objdetector.Model.ChannelsLoader;
import com.matis.objdetector.Model.VideoChannel.Channel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Set;

@RestController
@RequestMapping("channels")
public class ChannelsController {
    @GetMapping
    public ResponseEntity<HashMap<String,String>> allChannels(){
        Set<String> keys = ChannelsLoader.channelsContainer.channels.keySet();
        HashMap<String,String> channelsName = new HashMap<>();
        for (String key:keys){
            channelsName.put(key,ChannelsLoader.channelsContainer.channels.get(key).name);
        }
        if (channelsName.size()!=0){
            return new ResponseEntity<>(channelsName, HttpStatus.OK);
        }else{
            throw new NotFoundException();
        }
    }

    @GetMapping("{id}/enable")
    public ResponseEntity<ReqBoolValue> getEnable(@PathVariable String id){
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch!= null){
            return new ResponseEntity<>(new ReqBoolValue(ch.enableChannel.get()),HttpStatus.OK);
        }else{
            throw new NotFoundException();
        }
    }
    //fetch('/channels/8OhaeAVs/name'. { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({text: 'channnnneeeeelll'})}). then(console. log)
    @PutMapping("{id}/enable")
    public ResponseEntity<Succes> setEnable(@RequestBody ReqBoolValue value,@PathVariable String id){
        System.out.println("Ch-"+id+"-"+value.getValue());
        try{
            ChannelsLoader.channelsContainer.channels.get(id).enableChannel.set(value.getValue());
            return new ResponseEntity<>(new Succes(true),HttpStatus.OK);
        }catch (NullPointerException ex){
            return new ResponseEntity<>(new Succes(false),HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{id}/name")
    public ResponseEntity<HashMap<String, String>> getName(@PathVariable String id){
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch!= null){
            HashMap<String,String> result = new HashMap<String,String>(){{put(id,ch.name);}};
            return new ResponseEntity<>(result,HttpStatus.OK);
        }else{
            throw new NotFoundException();
        }
    }
    //fetch('/channels/8OhaeAVs/name'. { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({text: 'channnnneeeeelll'})}). then(console. log)
    @PutMapping("{id}/name")
    public ResponseEntity<Succes> setName(@RequestBody ReqStringValue value, @PathVariable String id){
        System.out.println("received name is -->" + value);
        try{
            ChannelsLoader.channelsContainer.channels.get(id).name=value.getValue();
            return new ResponseEntity<>(new Succes(true),HttpStatus.OK);
        }catch (NullPointerException ex){
            return new ResponseEntity<>(new Succes(false),HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{id}/number")
    public Integer getNumber(@PathVariable String id){
        Channel ch = ChannelsLoader.channelsContainer.channels.get(id);
        if (ch!= null){
            return ch.number.get();
        }else{
            throw new NotFoundException();
        }
    }



}
