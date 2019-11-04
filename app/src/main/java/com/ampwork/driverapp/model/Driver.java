package com.ampwork.driverapp.model;

public class Driver {

    public String bloodgroup;
    public String driverDL;
    public String driverId;
    public String driverName;
    public String driverPhone;
    public String emergency;
    public String gender;
    public String institute;
    public String password;
    public String fcmToken;
    public String totalTrips;
    public String tripDistance;
    public Boolean loggedIn;

    public Driver() {
    }

    public Driver(String bloodgroup, String driverDL, String driverId, String driverName, String driverPhone, String emergency, String gender, String institute, String password, String fcmToken, String totalTrips, String tripDistance, Boolean loggedIn) {
        this.bloodgroup = bloodgroup;
        this.driverDL = driverDL;
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.emergency = emergency;
        this.gender = gender;
        this.institute = institute;
        this.password = password;
        this.fcmToken = fcmToken;
        this.totalTrips = totalTrips;
        this.tripDistance = tripDistance;
        this.loggedIn = loggedIn;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getTotalTrips() {
        return totalTrips;
    }

    public void setTotalTrips(String totalTrips) {
        this.totalTrips = totalTrips;
    }

    public String getTripDistance() {
        return tripDistance;
    }

    public void setTripDistance(String tripDistance) {
        this.tripDistance = tripDistance;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
