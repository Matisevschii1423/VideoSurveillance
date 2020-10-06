package com.matis.objdetector.Controllers.Helpers;

public class ResValue {
    private boolean succes;
    private String value;

    public ResValue(boolean succes, String value) {
        this.succes = succes;
        this.value = value;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
