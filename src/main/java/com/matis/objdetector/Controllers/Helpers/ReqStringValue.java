package com.matis.objdetector.Controllers.Helpers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReqStringValue {

    @JsonProperty("value")
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
