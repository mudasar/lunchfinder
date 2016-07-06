package uk.appinvent.lunchfinder.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by mudasar on 25/06/16.
 */
public class DishesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private LunchFinderDbHelper mOpenHelper;

    static final int USER = 300;
    static final int USER_BY_ID = 301;

    static final int DISH = 100;
    static final int DISH_BY_ID = 101;
    static final int DISH_WITH_CATEGORY = 102;
    static final int DISH_WITH_SPECIAL = 103;


    private static final SQLiteQueryBuilder sDishByCateogryQueryBuilder;


    static{
        sDishByCateogryQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        sDishByCateogryQueryBuilder.setTables(
                LunchContract.DishEntry.TABLE_NAME);
    }


    //dish.category = ?
    private static final String sDishAndCategorySelection =
            LunchContract.DishEntry.TABLE_NAME +
                    "." + LunchContract.DishEntry.COLUMN_CATEGORY + " = ? ";

    //dish.id = ?
    private static final String sDishByIdSelection =
            LunchContract.DishEntry.TABLE_NAME +
                    "." + LunchContract.DishEntry._ID + " = ? ";

    //dish.dish_of_day_number = ? and dish.is_dish_of_day = ?
    private static final String sDishSpecialsSelection =
           LunchContract.DishEntry.TABLE_NAME +
                    "." + LunchContract.DishEntry.COLUMN_DISH_OF_DAY_NUMBER + " = ?  and "  +
                   LunchContract.DishEntry.COLUMN_IS_DISH_OF_DAY + " = ? ";

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = LunchContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, LunchContract.PATH_DISH, DISH);
        matcher.addURI(authority, LunchContract.PATH_DISH + "/#", DISH_BY_ID);

        matcher.addURI(authority, LunchContract.PATH_DISH + "/*/#", DISH_WITH_SPECIAL);
        matcher.addURI(authority, LunchContract.PATH_DISH + "/*/*", DISH_WITH_CATEGORY);

        matcher.addURI(authority, LunchContract.PATH_USER, USER);
        matcher.addURI(authority, LunchContract.PATH_USER + "/#", USER_BY_ID);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new LunchFinderDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case DISH_WITH_SPECIAL:
            {
                retCursor = getSpecialDishes(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case DISH_WITH_CATEGORY: {
                retCursor = getDishByCategory(uri, projection, sortOrder);
                break;
            }
            // "dish"
            case DISH: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        LunchContract.DishEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case DISH_BY_ID:
               Integer id =  LunchContract.DishEntry.getDishIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        LunchContract.DishEntry.TABLE_NAME,
                        projection,
                        sDishByIdSelection,
                        new String[]{Integer.toString(id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            // "user"
            case USER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        LunchContract.UserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getSpecialDishes(Uri uri, String[] projection, String sortOrder) {
        int dayOfWeek = LunchContract.DishEntry.getDayOfWeekFromUri(uri);

        return sDishByCateogryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sDishSpecialsSelection,
                new String[]{Integer.toString(dayOfWeek), Integer.toString(1)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getDishByCategory(Uri uri, String[] projection, String sortOrder) {
        String category = LunchContract.DishEntry.getCategoryFromUri(uri);

        return sDishByCateogryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sDishAndCategorySelection,
                new String[]{category},
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case DISH_WITH_SPECIAL :
                return LunchContract.DishEntry.CONTENT_ITEM_TYPE;
            case DISH_WITH_CATEGORY:
                return LunchContract.DishEntry.CONTENT_TYPE;
            case DISH:
                return LunchContract.DishEntry.CONTENT_TYPE;
            case USER:
                return LunchContract.UserEntry.CONTENT_TYPE;
            case USER_BY_ID:
                return LunchContract.UserEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case DISH: {

                long _id = db.insert(LunchContract.DishEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = LunchContract.DishEntry.buildDishUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case USER: {
                long _id = db.insert(LunchContract.UserEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = LunchContract.UserEntry.buildUserUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case DISH:
                rowsDeleted = db.delete(
                        LunchContract.DishEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case USER:
                rowsDeleted = db.delete(
                        LunchContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case DISH:

                rowsUpdated = db.update(LunchContract.DishEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case USER:
                rowsUpdated = db.update(LunchContract.UserEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DISH:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(LunchContract.DishEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
