package uk.appinvent.lunchfinder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mudasar on 20/06/16.
 */
public class LunchFinderDbHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "lunch.db";


    public LunchFinderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
// Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_DISH_TABLE = "CREATE TABLE " + LunchContract.DishEntry.TABLE_NAME + " (" +
                LunchContract.DishEntry._ID + " INTEGER PRIMARY KEY," +
                LunchContract.DishEntry.COLUMN_REF_ID + " INTEGER UNIQUE NOT NULL, " +
                LunchContract.DishEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                LunchContract.DishEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                LunchContract.DishEntry.COLUMN_WAITING_TIME + " INTEGER NOT NULL, " +
                LunchContract.DishEntry.COLUMN_PRICE + " REAL NOT NULL, " +
                LunchContract.DishEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                LunchContract.DishEntry.COLUMN_SHORT_DESCRIPTION + " TEXT NOT NULL, " +
                LunchContract.DishEntry.COLUMN_IS_DISH_OF_DAY + " INTEGER NOT NULL, " +
                LunchContract.DishEntry.COLUMN_DISH_OF_DAY_NUMBER + " INTEGER NOT NULL, " +
                LunchContract.DishEntry.COLUMN_CATEGORY + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + LunchContract.UserEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                LunchContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                LunchContract.UserEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                LunchContract.UserEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                LunchContract.UserEntry.COLUMN_PHONE + " TEXT NOT NULL " + ");";

        db.execSQL(SQL_CREATE_DISH_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + LunchContract.DishEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LunchContract.UserEntry.TABLE_NAME);
        onCreate(db);
    }
}
