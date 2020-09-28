package com.matis.objdetector.Controllers.Helpers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReqIntValue {
    @JsonProperty("value")
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
