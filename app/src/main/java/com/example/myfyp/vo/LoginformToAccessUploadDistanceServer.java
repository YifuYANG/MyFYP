package com.example.myfyp.vo;


public class LoginformToAccessUploadDistanceServer {
    private String deviceid;
    private String password;
    public LoginformToAccessUploadDistanceServer(String deviceid, String password) {
        this.deviceid = deviceid;
        this.password = password;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
