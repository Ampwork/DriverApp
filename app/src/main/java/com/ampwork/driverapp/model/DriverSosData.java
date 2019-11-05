package com.ampwork.driverapp.model;

public class DriverSosData {

    public String bloodgroup;
    public String driverDL;
    public String driverId;
    public String driverName;
    public String driverPhone;
    public String emergency;
    public String busName;
    public String busNumber;
    public String routeId;
    public String routeName;
    public String location;
    public String dateTime;

    public DriverSosData() {
    }

    public DriverSosData(String bloodgroup, String driverDL, String driverId, String driverName, String driverPhone, String emergency, String busName, String busNumber, String routeId, String routeName, String location, String dateTime) {
        this.bloodgroup = bloodgroup;
        this.driverDL = driverDL;
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.emergency = emergency;
        this.busName = busName;
        this.busNumber = busNumber;
        this.routeId = routeId;
        this.routeName = routeName;
        this.location = location;
        this.dateTime = dateTime;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public String getDriverDL() {
        return driverDL;
    }

    public void setDriverDL(String driverDL) {
        this.driverDL = driverDL;
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

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
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

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
