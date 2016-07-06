package uk.appinvent.lunchfinder.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by mudasar on 25/06/16.
 */
public class LunchContract {

    private final static String LOG_TAG = LunchContract.class.getSimpleName();

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "uk.appinvent.lunchfinder";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_USER = "user";
    public static final String PATH_DISH = "dish";
    public static final String PATH_ORDER = "order";


    public static final class DishEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DISH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DISH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DISH;



        // Table name
        public static final String TABLE_NAME = "dish";

        public static final String COLUMN_REF_ID = "ref_id";

        // name of the dish
        public static final String COLUMN_NAME = "name";

        // Dish description
        public static final String COLUMN_DESCRIPTION = "description";

        // waiting time for each dish
        public static final String COLUMN_WAITING_TIME = "waiting_time";

        // price of the dish
        public static final String COLUMN_PRICE = "price";

        // image url of the dish
        public static final String COLUMN_IMAGE_URL = "image_url";

        // short description of the dish
        public static final String COLUMN_SHORT_DESCRIPTION = "short_description";

        // is it dish of the day
        public static final String COLUMN_IS_DISH_OF_DAY = "is_dish_of_day";

        // dish of the day number in the week
        public static final String COLUMN_DISH_OF_DAY_NUMBER = "dish_of_day_number";

        // category each dish
        public static final String COLUMN_CATEGORY = "category";

        public static final String DEFAULT_SORT = COLUMN_CATEGORY + " DESC";

        public static Uri buildDishUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getCategoryFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static int getDayOfWeekFromUri(Uri uri) {
            Log.d(LOG_TAG, uri.toString());
            return Integer.parseInt(uri.getPathSegments().get(2));
        }

        public static Uri buildSpecialUri(int dayOfWeek) {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_DISH_OF_DAY_NUMBER).appendPath(Integer.toString(dayOfWeek)).build();
        }

        public static Uri buildCategoryUri(String category) {
            return CONTENT_URI.buildUpon().appendPath("category").appendPath(category).build();
        }

        public static Integer getDishIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }

    public static final class UserEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        // Table name
        public static final String TABLE_NAME = "user";

        // name of the user
        public static final String COLUMN_NAME = "name";

        // user's emal
        public static final String COLUMN_EMAIL = "email";

        // user's phone
        public static final String COLUMN_PHONE = "phone";

        public static final String COLUMN_PASSWORD = "password";

        public static final String COLUMN_UID = "uid";

        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }



}
