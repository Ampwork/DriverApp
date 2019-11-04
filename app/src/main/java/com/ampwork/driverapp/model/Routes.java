package com.ampwork.driverapp.model;

import java.util.ArrayList;

public class Routes {

    public String routeId;
    public String routeName;
    public String startingPoint;
    public String endingPoint;
    public ArrayList<BusStops> busStops;

    public Routes() {
    }

    public Routes(String routeId, String routeName, String startingPoint, String endingPoint, ArrayList<BusStops> busStops) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.startingPoint = startingPoint;
        this.endingPoint = endingPoint;
        this.busStops = busStops;
    }

    public ArrayList<BusStops> getBusStops() {
        return busStops;
    }

    public void setBusStops(ArrayList<BusStops> busStops) {
        this.busStops = busStops;
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

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getEndingPoint() {
        return endingPoint;
    }

    public void setEndingPoint(String endingPoint) {
        this.endingPoint = endingPoint;
    }
}
