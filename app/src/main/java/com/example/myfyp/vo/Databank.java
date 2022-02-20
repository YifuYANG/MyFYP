package com.example.myfyp.vo;



public class Databank {
    private String driverlicense;
    private String deviceid;

    public Databank(String driverlicense, String deviceid) {
        this.driverlicense = driverlicense;
        this.deviceid = deviceid;
    }

    public String getDriverlicense() {
        return driverlicense;
    }

    public void setDriverlicense(String driverlicense) {
        this.driverlicense = driverlicense;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }
}
