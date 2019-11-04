package com.ampwork.driverapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BusStops implements Parcelable {

    public String busStopOrder;
    public String busStopName;
    public String latitude;
    public String longitude;
    public String routeId;
    public String tripTime;
    public String distance;
    public String arrivalTime;

    public BusStops() {
    }

    public BusStops(String busStopOrder, String bustStopName, String latitude, String longitude, String routeId, String tripTime, String distance, String arrivalTime) {
        this.busStopOrder = busStopOrder;
        this.busStopName = bustStopName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.routeId = routeId;
        this.tripTime = tripTime;
        this.distance = distance;
        this.arrivalTime = arrivalTime;
    }

    protected BusStops(Parcel in) {
        busStopOrder = in.readString();
        busStopName = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        routeId = in.readString();
        tripTime = in.readString();
        distance = in.readString();
        arrivalTime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(busStopOrder);
        dest.writeString(busStopName);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(routeId);
        dest.writeString(tripTime);
        dest.writeString(distance);
        dest.writeString(arrivalTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BusStops> CREATOR = new Creator<BusStops>() {
        @Override
        public BusStops createFromParcel(Parcel in) {
            return new BusStops(in);
        }

        @Override
        public BusStops[] newArray(int size) {
            return new BusStops[size];
        }
    };

    public String getBusStopOrder() {
        return busStopOrder;
    }

    public void setBusStopOrder(String busStopOrder) {
        this.busStopOrder = busStopOrder;
    }

    public String getBusStopName() {
        return busStopName;
    }

    public void setBusStopName(String busStopName) {
        this.busStopName = busStopName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getTripTime() {
        return tripTime;
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}

