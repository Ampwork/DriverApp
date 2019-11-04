package com.ampwork.driverapp.model;

public class BusPath {

    String path;
    Boolean trackEnabled;
    Boolean destinationReached;
    Boolean tripCompleted;
    String busLocation;
    String nextStopId;
    String nextStopName;

    public BusPath() {
    }

    public BusPath(String path, Boolean trackEnabled, Boolean destinationReached, Boolean tripCompleted, String busLocation, String nextStopId, String nextStopName) {
        this.path = path;
        this.trackEnabled = trackEnabled;
        this.destinationReached = destinationReached;
        this.tripCompleted = tripCompleted;
        this.busLocation = busLocation;
        this.nextStopId = nextStopId;
        this.nextStopName = nextStopName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getTrackEnabled() {
        return trackEnabled;
    }

    public void setTrackEnabled(Boolean trackEnabled) {
        this.trackEnabled = trackEnabled;
    }

    public Boolean getDestinationReached() {
        return destinationReached;
    }

    public void setDestinationReached(Boolean destinationReached) {
        this.destinationReached = destinationReached;
    }

    public Boolean getTripCompleted() {
        return tripCompleted;
    }

    public void setTripCompleted(Boolean tripCompleted) {
        this.tripCompleted = tripCompleted;
    }

    public String getBusLocation() {
        return busLocation;
    }

    public void setBusLocation(String busLocation) {
        this.busLocation = busLocation;
    }

    public String getNextStopId() {
        return nextStopId;
    }

    public void setNextStopId(String nextStopId) {
        this.nextStopId = nextStopId;
    }

    public String getNextStopName() {
        return nextStopName;
    }

    public void setNextStopName(String nextStopName) {
        this.nextStopName = nextStopName;
    }
}
