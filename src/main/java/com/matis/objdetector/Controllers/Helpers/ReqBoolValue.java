package com.matis.objdetector.Controllers.Helpers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReqBoolValue {

    @JsonProperty("value")
    private boolean value;

    public ReqBoolValue(){

    }
    public ReqBoolValue(boolean value){
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

}
