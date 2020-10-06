package com.matis.objdetector.Controllers.Helpers;

import org.springframework.stereotype.Component;

@Component
public class RecValue {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
