package org.septa.android.app.managers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.septa.android.app.databases.SEPTADatabase;
import org.septa.android.app.models.RouteTypes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Future handler of database interactions
 * can become singleton when all calls are changed and a database instance can live here instead
 * of being passed in
 * <p/>
 * Created by Antoine on 12/29/2014.
 */
public class DatabaseManager {

    private static final String TAG = SEPTADatabase.class.getName();
    private static final String QUERY_BUS_HOLIDAY = "SELECT service_id, date FROM holiday_bus WHERE date='%s'";
    private static final String QUERY_RAIL_HOLIDAY = "SELECT service_id, date FROM holiday_rail WHERE date='%s'";
    private static final String DATE_FORMAT_HOLIDAY = "yyyyMMdd";

    /**
     * Check if the passed in date is a holiday
     *
     * @param database Database object
     * @param date     Date object
     * @return the corresponding service id, otherwise -1 if day not found
     */
    public static int isTodayBusHoliday(SQLiteDatabase database, Date date) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_HOLIDAY);
        String query = String.format(QUERY_BUS_HOLIDAY, dateFormat.format(date));
        int serviceId = -1;
        try {
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    serviceId = cursor.getInt(0);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying holiday", e);
        }

        return serviceId;
    }

    /**
     * Check if the passed in date is a holiday
     *
     * @param database Database object
     * @param date     Date object
     * @return the corresponding service id, otherwise -1 if day not found
     */
    public static int isTodayRailHoliday(SQLiteDatabase database, Date date) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_HOLIDAY);
        String query = String.format(QUERY_RAIL_HOLIDAY, dateFormat.format(date));
        int serviceId = -1;
        try {
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    serviceId = cursor.getInt(0);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying holiday", e);
        }

        return serviceId;
    }

    /**
     * Retrieves the service id for the specified day of the week
     *
     * @param database database object
     * @param day day of the week constant from the Calendar class
     * @return service id
     */
    public static int serviceIdForDayOfWeek(SQLiteDatabase database, int day, RouteTypes routeType) {
        // Default to Friday(weekday)
        int dbDay = 2;
        switch (day) {
            case Calendar.SUNDAY:
                dbDay = 64;
                break;
            case Calendar.MONDAY:
                dbDay = 32;
                break;
            case Calendar.TUESDAY:
                dbDay = 16;
                break;
            case Calendar.WEDNESDAY:
                dbDay = 8;
                break;
            case Calendar.THURSDAY:
                dbDay = 4;
                break;
            case Calendar.FRIDAY:
                dbDay = 2;
                break;
            case Calendar.SATURDAY:
                dbDay = 1;
                break;
        }

        int serviceId = 1;
        String query;
        if(routeType == RouteTypes.RAIL) {
            query = "SELECT service_id, days FROM calendar_rail WHERE (days &" + dbDay + ")";
        } else {
            query = "SELECT service_id, days FROM calendar_bus WHERE (days &" + dbDay + ")";
        }
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, null);
            if(cursor != null && cursor.moveToFirst()) {
                serviceId = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching service id", e);
        }

        return serviceId;
    }

    /**
     * Return the nearest date of the passed in day
     *
     * @param dayOfWeek day of the week constant from the Calendar class
     * @return date representing the nearest day
     */
    public static Date nearestDayOfWeek(int dayOfWeek) {
        Calendar calendar = new GregorianCalendar();
        for (int i = 0; i < 7; i++) {
            if (calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                break;
            } else {
                calendar.add(Calendar.DATE, 1);
            }
        }
        return calendar.getTime();
    }

}
