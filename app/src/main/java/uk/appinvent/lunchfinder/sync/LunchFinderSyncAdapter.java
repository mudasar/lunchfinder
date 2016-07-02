package uk.appinvent.lunchfinder.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import uk.appinvent.lunchfinder.R;
import uk.appinvent.lunchfinder.data.Dish;
import uk.appinvent.lunchfinder.data.LunchContract;

/**
 * Created by mudasar on 24/06/16.
 */
public class LunchFinderSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = LunchFinderSyncAdapter.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED =
            "uk.appinvent.lunchfinder.ACTION_DATA_UPDATED";

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int LUNCH_NOTIFICATION_ID = 3004;

    public static final int LUNCH_STATUS_OK = 0;
    public static final int LUNCH_STATUS_SERVER_DOWN = 1;
    public static final int LUNCH_STATUS_SERVER_INVALID = 2;
    public static final int LUNCH_STATUS_UNKNOWN = 3;
    public static final int LUNCH_STATUS_INVALID = 4;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LUNCH_STATUS_OK, LUNCH_STATUS_SERVER_DOWN, LUNCH_STATUS_SERVER_INVALID,  LUNCH_STATUS_UNKNOWN, LUNCH_STATUS_INVALID})
    public @interface ApiStatus {}

    public LunchFinderSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(LOG_TAG, "Starting sync");

        // We no longer need just the location String, but also potentially the latitude and
        // longitude, in case we are syncing based on a new Place Picker API result.
        Context context = getContext();

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String dishesJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String FORECAST_BASE_URL =
                    "http://lunchfinder.esy.es/data.json";


            Uri.Builder uriBuilder = Uri.parse(FORECAST_BASE_URL).buildUpon();


            Uri builtUri = uriBuilder
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                setApiStatus(getContext(), LUNCH_STATUS_SERVER_DOWN);
                return;
            }
            dishesJsonStr = buffer.toString();
            getDishesDataFromJson(dishesJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            setApiStatus(getContext(), LUNCH_STATUS_SERVER_DOWN);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            setApiStatus(getContext(), LUNCH_STATUS_SERVER_INVALID);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }

    private void getDishesDataFromJson(String dishesJsonStr) throws JSONException{

        final String API_MESSAGE_CODE = "error";

        try {

          final   JSONArray array = new JSONArray(dishesJsonStr);


            if (dishesJsonStr.contains(API_MESSAGE_CODE)){
                JSONObject errorObject = array.getJSONObject(0);

                int errorCode = errorObject.getInt(API_MESSAGE_CODE);

                switch (errorCode) {
                    case HttpURLConnection.HTTP_OK:
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        setApiStatus(getContext(), LUNCH_STATUS_INVALID);
                        return;
                    default:
                        setApiStatus(getContext(), LUNCH_STATUS_SERVER_DOWN);
                        return;
                }
            }

            Type collectionType = new TypeToken<List<Dish>>(){}.getType();
            Gson gson = new Gson();
            final    List<Dish> dishesList = gson.fromJson(dishesJsonStr, collectionType);


            if (dishesList.size() > 0){
                for (Dish dish :
                        dishesList) {
                    long dishId;

                    // First, check if the location with this city name exists in the db
                    Cursor dishCursor = getContext().getContentResolver().query(
                            LunchContract.DishEntry.CONTENT_URI,
                            new String[]{LunchContract.DishEntry._ID, LunchContract.DishEntry.COLUMN_REF_ID},
                            LunchContract.DishEntry.COLUMN_REF_ID + " = ?",
                            new String[]{Long.toString(dish.getRefId())},
                            null);

                    if (dishCursor != null && dishCursor.moveToFirst()) {
                        int dishIdIndex = dishCursor.getColumnIndex(LunchContract.DishEntry._ID);
                        dishId = dishCursor.getLong(dishIdIndex);
                    } else {
                        // Now that the content provider is set up, inserting rows of data is pretty simple.
                        // First create a ContentValues object to hold the data you want to insert.
                        ContentValues dishValues = new ContentValues();

                        // Then add the data, along with the corresponding name of the data type,
                        // so the content provider knows what kind of value is being inserted.
                        dishValues.put(LunchContract.DishEntry.COLUMN_REF_ID, dish.getRefId());
                        dishValues.put(LunchContract.DishEntry.COLUMN_NAME, dish.getName());
                        dishValues.put(LunchContract.DishEntry.COLUMN_DESCRIPTION, dish.getDescription());
                        dishValues.put(LunchContract.DishEntry.COLUMN_WAITING_TIME, dish.getWaitingTime());
                        dishValues.put(LunchContract.DishEntry.COLUMN_PRICE, dish.getPrice());
                        dishValues.put(LunchContract.DishEntry.COLUMN_IMAGE_URL, dish.getImageUrl());
                        dishValues.put(LunchContract.DishEntry.COLUMN_SHORT_DESCRIPTION, dish.getShortDescription());
                        dishValues.put(LunchContract.DishEntry.COLUMN_IS_DISH_OF_DAY, dish.getDishOftheDay());
                        dishValues.put(LunchContract.DishEntry.COLUMN_DISH_OF_DAY_NUMBER, dish.getDishOfDayNumber());
                        dishValues.put(LunchContract.DishEntry.COLUMN_CATEGORY, dish.getCategory());

                        // Finally, insert location data into the database.
                        Uri insertedUri = getContext().getContentResolver().insert(
                                LunchContract.DishEntry.CONTENT_URI,
                                dishValues
                        );

                        // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
                        dishId = ContentUris.parseId(insertedUri);
                        Log.d(LOG_TAG, "Dish id inserted is " + dishId);
                    }

                    dishCursor.close();
                }
            }

            Log.d(LOG_TAG, "Sync Complete. " + array.length() + " Inserted");
            setApiStatus(getContext(), LUNCH_STATUS_OK);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            setApiStatus(getContext(), LUNCH_STATUS_SERVER_INVALID);
        }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        LunchFinderSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Sets the location status into shared preference.  This function should not be called from
     * the UI thread because it uses commit to write to the shared preferences.
     * @param c Context to get the PreferenceManager from.
     * @param locationStatus The IntDef value to set
     */
    static private void setApiStatus(Context c, @ApiStatus int locationStatus){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_lunch_status_key), locationStatus);
        spe.commit();
    }
}
