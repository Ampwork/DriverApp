package com.ampwork.driverapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ampwork.driverapp.model.BusStops;
import com.ampwork.driverapp.model.Notification;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance = null;
    private static SQLiteDatabase sqLiteDatabase = null;

    private static final String DB_NAME = "driverapp.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_BUS_STOPS = "busstopstb";
    private static final String STOP_ORDER = "stoporder";
    private static final String STOP_NAME = "stopname";
    private static final String STOP_LATITUDE = "stoplatitude";
    private static final String STOP_LONGITUDE = "stoplongitude";
    private static final String ROUTE_ID = "routeId";
    private static final String DISTANCE = "distance";
    private static final String TRIP_TIME = "tripTime";
    private static final String ARRIVAL_TIME = "arrivalTime";

    private static final String TABLE_NOTIFICATIONS = "notificationtb";
    private static final String NOTIFICATION_TITLE = "notification_title";
    private static final String NOTIFICATION_BODY = "notification_body";
    private static final String NOTIFICATION_DATE = "notification_date";



    public static void init(Context ctx) {
        if (instance == null) {
            instance = new DBHelper(ctx);
        }
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    private static SQLiteDatabase getDatabase() {
        if (sqLiteDatabase == null) {
            sqLiteDatabase = instance.getWritableDatabase();
        }
        return sqLiteDatabase;
    }

    public static void deactivate() {
        if (null != instance && sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
        }
        instance = null;
        sqLiteDatabase = null;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getTableGeofenceStopsTable());
        db.execSQL(getNotificationTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUS_STOPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
    }

    private String getTableGeofenceStopsTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_BUS_STOPS + " ( "
                + STOP_ORDER + " TEXT NOT NULL, "
                + STOP_NAME + " TEXT NOT NULL, "
                + STOP_LATITUDE + " TEXT NOT NULL, "
                + STOP_LONGITUDE + " TEXT, "
                + ROUTE_ID + " TEXT, "
                + DISTANCE + " TEXT, "
                + TRIP_TIME + " TEXT, "
                + ARRIVAL_TIME + " TEXT )";

    }

    private String getNotificationTable(){
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + " ( "
                + NOTIFICATION_TITLE + " TEXT NOT NULL, "
                + NOTIFICATION_BODY + " TEXT NOT NULL, "
                + NOTIFICATION_DATE + " TEXT )";
    }


    /*BusStops Data*/
    public static void addStops(BusStops busStops){

        SQLiteDatabase db = getDatabase();
        ContentValues cv = new ContentValues();

        Cursor cr = null;
        cv.put(STOP_ORDER, busStops.getBusStopOrder());
        cv.put(STOP_NAME, busStops.getBusStopName());
        cv.put(STOP_LATITUDE, busStops.getLatitude());
        cv.put(STOP_LONGITUDE, busStops.getLongitude());
        cv.put(ROUTE_ID, busStops.getRouteId());
        cv.put(DISTANCE, busStops.getDistance());
        cv.put(TRIP_TIME, busStops.getTripTime());
        cv.put(ARRIVAL_TIME, busStops.getArrivalTime());


        cr = getDatabase().query(TABLE_BUS_STOPS, null, STOP_ORDER + " = ?", new String[]{busStops.getBusStopOrder()}, null, null, null);

        if (null != cr && cr.moveToFirst()) {
            db.update(TABLE_BUS_STOPS, cv, STOP_ORDER + " = ?", new String[]{busStops.getBusStopOrder()});
        } else {
            db.insert(TABLE_BUS_STOPS, null, cv);
        }

        if (cr != null && !cr.isClosed()) {
            cr.close();
        }

    }

    public static void deleteGeofenceShopsTable() {
        SQLiteDatabase db = getDatabase();
        db.delete(TABLE_BUS_STOPS, null, null);
    }

    public static ArrayList<BusStops> getAllBusStops(){

        ArrayList<BusStops> busStopsArrayList = new ArrayList<BusStops>();

        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.query(TABLE_BUS_STOPS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                BusStops busStops = new BusStops();

                String stop_order = cursor.getString(cursor.getColumnIndex(STOP_ORDER));
                String stop_name = cursor.getString(cursor.getColumnIndex(STOP_NAME));
                String stop_latitude = cursor.getString(cursor.getColumnIndex(STOP_LATITUDE));
                String stop_longitude = cursor.getString(cursor.getColumnIndex(STOP_LONGITUDE));
                String route_id = cursor.getString(cursor.getColumnIndex(ROUTE_ID));
                String distance = cursor.getString(cursor.getColumnIndex(DISTANCE));
                String trip_time = cursor.getString(cursor.getColumnIndex(TRIP_TIME));
                String arrival_time = cursor.getString(cursor.getColumnIndex(ARRIVAL_TIME));

                busStops.setBusStopOrder(stop_order);
                busStops.setBusStopName(stop_name);
                busStops.setLatitude(stop_latitude);
                busStops.setLongitude(stop_longitude);
                busStops.setRouteId(route_id);
                busStops.setDistance(distance);
                busStops.setTripTime(trip_time);
                busStops.setArrivalTime(arrival_time);

                busStopsArrayList.add(busStops);

            } while (cursor.moveToNext());

        }

        if (cursor != null) {
            cursor.close();
        }


        return busStopsArrayList;
    }

    public static BusStops getBusStopData(String stop_order) {
        BusStops busStops = new BusStops();

        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.query(TABLE_BUS_STOPS, null, STOP_ORDER + "=?", new String[]{stop_order}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            busStops.setBusStopOrder(cursor.getString(cursor.getColumnIndex(STOP_ORDER)));
            busStops.setBusStopName(cursor.getString(cursor.getColumnIndex(STOP_NAME)));
            busStops.setLatitude(cursor.getString(cursor.getColumnIndex(STOP_LATITUDE)));
            busStops.setLongitude(cursor.getString(cursor.getColumnIndex(STOP_LONGITUDE)));
            busStops.setRouteId(cursor.getString(cursor.getColumnIndex(ROUTE_ID)));
            busStops.setDistance(cursor.getString(cursor.getColumnIndex(DISTANCE)));
            busStops.setTripTime(cursor.getString(cursor.getColumnIndex(TRIP_TIME)));
            busStops.setArrivalTime(cursor.getString(cursor.getColumnIndex(ARRIVAL_TIME)));
        }

        if (cursor != null) {
            cursor.close();
        }
        return busStops;
    }

    /*Notification Data*/
    public static void addNotification(Notification notification){

        SQLiteDatabase db = getDatabase();
        ContentValues cv = new ContentValues();

        Cursor cr = null;
        cv.put(NOTIFICATION_TITLE, "trackit 360");
        cv.put(NOTIFICATION_BODY, notification.getMessage());
        cv.put(NOTIFICATION_DATE, notification.getDate());


        cr = getDatabase().query(TABLE_NOTIFICATIONS, null, NOTIFICATION_DATE + " = ?", new String[]{notification.getDate()}, null, null, null);

        if (null != cr && cr.moveToFirst()) {
            db.update(TABLE_NOTIFICATIONS, cv, NOTIFICATION_DATE + " = ?", new String[]{notification.getDate()});
        } else {
            db.insert(TABLE_NOTIFICATIONS, null, cv);
        }

        if (cr != null && !cr.isClosed()) {
            cr.close();
        }

    }

    public static void deleteNotificationTable() {
        SQLiteDatabase db = getDatabase();
        db.delete(TABLE_NOTIFICATIONS, null, null);
    }

    public static ArrayList<Notification> getAllNotifications(){

        ArrayList<Notification> notificationArrayList = new ArrayList<Notification>();

        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.query(TABLE_NOTIFICATIONS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();

                String notification_date = cursor.getString(cursor.getColumnIndex(NOTIFICATION_DATE));
                String notification_title = cursor.getString(cursor.getColumnIndex(NOTIFICATION_TITLE));
                String notification_body = cursor.getString(cursor.getColumnIndex(NOTIFICATION_BODY));

                notification.setDate(notification_date);
               // notification.setTitle(notification_title);
                notification.setMessage(notification_body);

                notificationArrayList.add(notification);

            } while (cursor.moveToNext());

        }

        if (cursor != null) {
            cursor.close();
        }


        return notificationArrayList;
    }

}
