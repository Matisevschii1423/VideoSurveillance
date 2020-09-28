package com.matis.objdetector.Controllers.Helpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Succes {
    @JsonProperty("succes")
    Boolean succes = false;

    public Succes(){

    }
    public Succes(boolean value){
        this.succes = value;
    }

    public Boolean getSucces() {
        return succes;
    }

    public void setSucces(Boolean succes) {
        this.succes = succes;
    }
}
