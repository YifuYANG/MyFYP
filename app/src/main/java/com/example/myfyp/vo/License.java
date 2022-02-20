package com.example.myfyp.vo;

public class License {

    private String driverlicense;
    private String deviceId;
    private String password;

    public License(String deviceId, String driverlicense,String password) {
        this.deviceId = deviceId;
        this.driverlicense = driverlicense;
        this.password = password;

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverlicense() {
        return driverlicense;
    }

    public void setDriverlicense(String driverlicense) {
        this.driverlicense = driverlicense;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
