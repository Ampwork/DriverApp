package com.ampwork.driverapp.model;

public class Fuel {

    private String amount;
    private String busName;
    private String busNumber;
    private String busReading;
    private String date;
    private String driverId;
    private String driverName;
    private String fuel;
    private String odometer;
    private String previousReading;

    public Fuel() {
    }

    public Fuel(String amount, String busName, String busNumber, String busReading, String date, String driverId, String driverName, String fuel, String odometer, String previousReading) {
        this.amount = amount;
        this.busName = busName;
        this.busNumber = busNumber;
        this.busReading = busReading;
        this.date = date;
        this.driverId = driverId;
        this.driverName = driverName;
        this.fuel = fuel;
        this.odometer = odometer;
        this.previousReading = previousReading;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getBusReading() {
        return busReading;
    }

    public void setBusReading(String busReading) {
        this.busReading = busReading;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public String getOdometer() {
        return odometer;
    }

    public void setOdometer(String odometer) {
        this.odometer = odometer;
    }

    public String getPreviousReading() {
        return previousReading;
    }

    public void setPreviousReading(String previousReading) {
        this.previousReading = previousReading;
    }
}
