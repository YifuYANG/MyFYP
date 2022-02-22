package com.example.myfyp.entity;

import java.util.ArrayList;

public class Userinfo {
    private String driverlicense;
    private String email;
    private ArrayList<String> trustDeviceId;
    private String password;
    public Userinfo() {
    }

    public Userinfo(String driverlicense, String email, ArrayList<String> trustDeviceId,String password) {
        this.driverlicense = driverlicense;
        this.email = email;
        this.trustDeviceId = trustDeviceId;
        this.password=password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getTrustDeviceId() {
        return trustDeviceId;
    }

    public void setTrustDeviceId(ArrayList<String> trustDeviceId) {
        this.trustDeviceId = trustDeviceId;
    }
}