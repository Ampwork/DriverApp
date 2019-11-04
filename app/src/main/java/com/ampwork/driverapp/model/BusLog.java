package com.ampwork.driverapp.model;

public class BusLog {

    private String arrivedTime;
    private String busName;
    private String busNumber;
    private String busPath;
    private String date;
    private String departTime;
    private String direction;
    private String driverId;
    private String driverName;
    private String routeName;
    private String tripCompleted;
    private String tripDistance;
    private String tripDuration;
    private String busStopsCovered;

    public BusLog() {
    }

    public BusLog(String arrivedTime, String busName, String busNumber, String busPath, String date, String departTime, String direction, String driverId, String driverName, String routeName, String tripCompleted, String tripDistance, String tripDuration, String busStopsCovered) {
        this.arrivedTime = arrivedTime;
        this.busName = busName;
        this.busNumber = busNumber;
        this.busPath = busPath;
        this.date = date;
        this.departTime = departTime;
        this.direction = direction;
        this.driverId = driverId;
        this.driverName = driverName;
        this.routeName = routeName;
        this.tripCompleted = tripCompleted;
        this.tripDistance = tripDistance;
        this.tripDuration = tripDuration;
        this.busStopsCovered = busStopsCovered;
    }

    public String getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(String arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusPath() {
        return busPath;
    }

    public void setBusPath(String busPath) {
        this.busPath = busPath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDepartTime() {
        return departTime;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getTripCompleted() {
        return tripCompleted;
    }

    public void setTripCompleted(String tripCompleted) {
        this.tripCompleted = tripCompleted;
    }

    public String getTripDistance() {
        return tripDistance;
    }

    public void setTripDistance(String tripDistance) {
        this.tripDistance = tripDistance;
    }

    public String getTripDuration() {
        return tripDuration;
    }

    public void setTripDuration(String tripDuration) {
        this.tripDuration = tripDuration;
    }

    public String getBusStopsCovered() {
        return busStopsCovered;
    }

    public void setBusStopsCovered(String busStopsCovered) {
        this.busStopsCovered = busStopsCovered;
    }
}
