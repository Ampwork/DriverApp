package com.ampwork.driverapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class LiveBusDetail implements Parcelable {

    public String busLocation;
    public String busName;
    public String busNumber;
    public String busPath;
    public String departureTime;
    public Boolean destinationReached;
    public String direction;
    public String driverId;
    public String driverName;
    public Boolean driving;
    public String endPoint;
    public String institute;
    public String nextStopName;
    public String nextStopOrderId;
    public String odometer;
    public String routeId;
    public String routeName;
    public String startPoint;
    public Boolean trackEnabled;
    public Boolean tripCompleted;
    public String sos;
    public Boolean driverSOS;
    public String bellCounts;
    public String totalDistance;
    public String totalFuel;
    public String totalTrips;
    public String tripDistance;
    public String  mileage;


    public LiveBusDetail() { }


    public LiveBusDetail(String busLocation, String busName, String busNumber, String busPath, String departureTime, Boolean destinationReached, String direction, String driverId, String driverName, Boolean driving, String endPoint, String institute, String nextStopName, String nextStopOrderId, String odometer, String routeId, String routeName, String startPoint, Boolean trackEnabled, Boolean tripCompleted, String sos, Boolean driverSOS, String bellCounts, String totalDistance, String totalFuel, String totalTrips, String tripDistance, String mileage) {
        this.busLocation = busLocation;
        this.busName = busName;
        this.busNumber = busNumber;
        this.busPath = busPath;
        this.departureTime = departureTime;
        this.destinationReached = destinationReached;
        this.direction = direction;
        this.driverId = driverId;
        this.driverName = driverName;
        this.driving = driving;
        this.endPoint = endPoint;
        this.institute = institute;
        this.nextStopName = nextStopName;
        this.nextStopOrderId = nextStopOrderId;
        this.odometer = odometer;
        this.routeId = routeId;
        this.routeName = routeName;
        this.startPoint = startPoint;
        this.trackEnabled = trackEnabled;
        this.tripCompleted = tripCompleted;
        this.sos = sos;
        this.driverSOS = driverSOS;
        this.bellCounts = bellCounts;
        this.totalDistance = totalDistance;
        this.totalFuel = totalFuel;
        this.totalTrips = totalTrips;
        this.tripDistance = tripDistance;
        this.mileage = mileage;
    }

    protected LiveBusDetail(Parcel in) {
        busLocation = in.readString();
        busName = in.readString();
        busNumber = in.readString();
        busPath = in.readString();
        departureTime = in.readString();
        byte tmpDestinationReached = in.readByte();
        destinationReached = tmpDestinationReached == 0 ? null : tmpDestinationReached == 1;
        direction = in.readString();
        driverId = in.readString();
        driverName = in.readString();
        byte tmpDriving = in.readByte();
        driving = tmpDriving == 0 ? null : tmpDriving == 1;
        endPoint = in.readString();
        institute = in.readString();
        nextStopName = in.readString();
        nextStopOrderId = in.readString();
        odometer = in.readString();
        routeId = in.readString();
        routeName = in.readString();
        startPoint = in.readString();
        byte tmpTrackEnabled = in.readByte();
        trackEnabled = tmpTrackEnabled == 0 ? null : tmpTrackEnabled == 1;
        byte tmpTripCompleted = in.readByte();
        tripCompleted = tmpTripCompleted == 0 ? null : tmpTripCompleted == 1;
        sos = in.readString();
        byte tmpDriverSOS = in.readByte();
        driverSOS = tmpDriverSOS == 0 ? null : tmpDriverSOS == 1;
        bellCounts = in.readString();
        totalDistance = in.readString();
        totalFuel = in.readString();
        totalTrips = in.readString();
        tripDistance = in.readString();
        mileage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(busLocation);
        dest.writeString(busName);
        dest.writeString(busNumber);
        dest.writeString(busPath);
        dest.writeString(departureTime);
        dest.writeByte((byte) (destinationReached == null ? 0 : destinationReached ? 1 : 2));
        dest.writeString(direction);
        dest.writeString(driverId);
        dest.writeString(driverName);
        dest.writeByte((byte) (driving == null ? 0 : driving ? 1 : 2));
        dest.writeString(endPoint);
        dest.writeString(institute);
        dest.writeString(nextStopName);
        dest.writeString(nextStopOrderId);
        dest.writeString(odometer);
        dest.writeString(routeId);
        dest.writeString(routeName);
        dest.writeString(startPoint);
        dest.writeByte((byte) (trackEnabled == null ? 0 : trackEnabled ? 1 : 2));
        dest.writeByte((byte) (tripCompleted == null ? 0 : tripCompleted ? 1 : 2));
        dest.writeString(sos);
        dest.writeByte((byte) (driverSOS == null ? 0 : driverSOS ? 1 : 2));
        dest.writeString(bellCounts);
        dest.writeString(totalDistance);
        dest.writeString(totalFuel);
        dest.writeString(totalTrips);
        dest.writeString(tripDistance);
        dest.writeString(mileage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LiveBusDetail> CREATOR = new Creator<LiveBusDetail>() {
        @Override
        public LiveBusDetail createFromParcel(Parcel in) {
            return new LiveBusDetail(in);
        }

        @Override
        public LiveBusDetail[] newArray(int size) {
            return new LiveBusDetail[size];
        }
    };

    public String getBusLocation() {
        return busLocation;
    }

    public void setBusLocation(String busLocation) {
        this.busLocation = busLocation;
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

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public Boolean getDestinationReached() {
        return destinationReached;
    }

    public void setDestinationReached(Boolean destinationReached) {
        this.destinationReached = destinationReached;
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

    public Boolean getDriving() {
        return driving;
    }

    public void setDriving(Boolean driving) {
        this.driving = driving;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getNextStopName() {
        return nextStopName;
    }

    public void setNextStopName(String nextStopName) {
        this.nextStopName = nextStopName;
    }

    public String getNextStopOrderId() {
        return nextStopOrderId;
    }

    public void setNextStopOrderId(String nextStopOrderId) {
        this.nextStopOrderId = nextStopOrderId;
    }

    public String getOdometer() {
        return odometer;
    }

    public void setOdometer(String odometer) {
        this.odometer = odometer;
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

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public Boolean getTrackEnabled() {
        return trackEnabled;
    }

    public void setTrackEnabled(Boolean trackEnabled) {
        this.trackEnabled = trackEnabled;
    }

    public Boolean getTripCompleted() {
        return tripCompleted;
    }

    public void setTripCompleted(Boolean tripCompleted) {
        this.tripCompleted = tripCompleted;
    }

    public String getSos() {
        return sos;
    }

    public void setSos(String sos) {
        this.sos = sos;
    }

    public Boolean getDriverSOS() {
        return driverSOS;
    }

    public void setDriverSOS(Boolean driverSOS) {
        this.driverSOS = driverSOS;
    }

    public String getBellCounts() {
        return bellCounts;
    }

    public void setBellCounts(String bellCounts) {
        this.bellCounts = bellCounts;
    }


    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getTotalFuel() {
        return totalFuel;
    }

    public void setTotalFuel(String totalFuel) {
        this.totalFuel = totalFuel;
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

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public static HashMap<String,Object> getLocationUpdateObj(String busLocation, String busPath, Boolean destinationReached, String direction, Boolean driving, String nextStopName, String nextStopOrderId, Boolean trackEnabled, Boolean tripCompleted){

        HashMap<String,Object> objectHashMap = new HashMap<>();
        objectHashMap.put("busLocation",busLocation);
        objectHashMap.put("busPath",busPath);
        objectHashMap.put("destinationReached",destinationReached);
        objectHashMap.put("direction",direction);
        objectHashMap.put("driving",driving);
        objectHashMap.put("nextStopName",nextStopName);
        objectHashMap.put("nextStopOrderId",nextStopOrderId);
        objectHashMap.put("trackEnabled",trackEnabled);
        objectHashMap.put("tripCompleted",tripCompleted);

        return objectHashMap;
    }

    // Next BusStops Update
    public static HashMap<String,Object> getBusStopsUpdateObj( Boolean destinationReached,String nextStopName, String nextStopOrderId, Boolean trackEnabled, Boolean tripCompleted){

        HashMap<String,Object> objectHashMap = new HashMap<>();
        objectHashMap.put("destinationReached",destinationReached);
        objectHashMap.put("nextStopName",nextStopName);
        objectHashMap.put("nextStopOrderId",nextStopOrderId);
        objectHashMap.put("trackEnabled",trackEnabled);
        objectHashMap.put("tripCompleted",tripCompleted);

        return objectHashMap;
    }

    public static HashMap<String,Object> getGeofenceEntranceObj( Boolean destinationReached,String nextStopName, String nextStopOrderId, Boolean trackEnabled, Boolean tripCompleted,String busPath){

        HashMap<String,Object> objectHashMap = new HashMap<>();
        objectHashMap.put("destinationReached",destinationReached);
        objectHashMap.put("nextStopName",nextStopName);
        objectHashMap.put("nextStopOrderId",nextStopOrderId);
        objectHashMap.put("trackEnabled",trackEnabled);
        objectHashMap.put("tripCompleted",tripCompleted);
        objectHashMap.put("busPath",busPath);

        return objectHashMap;
    }


    public static HashMap<String,Object> getResetBusTrackObj(String busLocation, String busPath, Boolean destinationReached, Boolean driving, String endPoint, String nextStopName, String nextStopOrderId, String startPoint, Boolean trackEnabled, Boolean tripCompleted, String bellCounts){

        HashMap<String,Object> objectHashMap = new HashMap<>();
        objectHashMap.put("busLocation",busLocation);
        objectHashMap.put("busPath",busPath);
        objectHashMap.put("destinationReached",destinationReached);
        objectHashMap.put("direction","");
        objectHashMap.put("driving",driving);
        objectHashMap.put("endPoint",endPoint);
        objectHashMap.put("nextStopName",nextStopName);
        objectHashMap.put("nextStopOrderId",nextStopOrderId);
        objectHashMap.put("startPoint",startPoint);
        objectHashMap.put("trackEnabled",trackEnabled);
        objectHashMap.put("tripCompleted",tripCompleted);
        objectHashMap.put("bellCounts",bellCounts);


        return objectHashMap;
    }



}
