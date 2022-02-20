package com.example.myfyp;

public class UploadedData {

    private double currentLatitude;
    private double currentLongitude;
    private double targetLatitude;
    private double targetLongitude;
    private double distance;


    public UploadedData(double currentLatitude, double currentLongitude, double targetLatitude, double targetLongitude,double distance) {
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.targetLatitude = targetLatitude;
        this.targetLongitude = targetLongitude;
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public double getTargetLatitude() {
        return targetLatitude;
    }

    public void setTargetLatitude(double targetLatitude) {
        this.targetLatitude = targetLatitude;
    }

    public double getTargetLongitude() {
        return targetLongitude;
    }

    public void setTargetLongitude(double targetLongitude) {
        this.targetLongitude = targetLongitude;
    }
}
