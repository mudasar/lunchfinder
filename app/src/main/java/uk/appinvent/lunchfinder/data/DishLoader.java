package uk.appinvent.lunchfinder.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;


/**
 * Created by mudasar on 27/06/16.
 */
public class DishLoader extends CursorLoader {

    public static DishLoader allDishesByCategoryInstance(Context context, String category) {
        return new DishLoader(context, LunchContract.DishEntry.buildCategoryUri(category));
    }

    public static DishLoader allSpecialDishesInstance(Context context, int dayOfWeek) {
        return new DishLoader(context, LunchContract.DishEntry.buildSpecialUri(dayOfWeek));
    }

    public static DishLoader newInstanceForDishId(Context context, long dishId) {
        return new DishLoader(context, LunchContract.DishEntry.buildDishUri(dishId));
    }

    public DishLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, LunchContract.DishEntry.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                LunchContract.DishEntry._ID,
                LunchContract.DishEntry.COLUMN_REF_ID,
                LunchContract.DishEntry.COLUMN_NAME,
                LunchContract.DishEntry.COLUMN_DESCRIPTION,
                LunchContract.DishEntry.COLUMN_WAITING_TIME,
                LunchContract.DishEntry.COLUMN_PRICE,
                LunchContract.DishEntry.COLUMN_IMAGE_URL,
                LunchContract.DishEntry.COLUMN_SHORT_DESCRIPTION,
                LunchContract.DishEntry.COLUMN_IS_DISH_OF_DAY,
                LunchContract.DishEntry.COLUMN_DISH_OF_DAY_NUMBER,
                LunchContract.DishEntry.COLUMN_CATEGORY
        };

        int _ID = 0;
        int COLUMN_REF_ID = 1;
        int COLUMN_NAME = 2;
        int COLUMN_DESCRIPTION = 3;
        int COLUMN_WAITING_TIME = 4;
        int COLUMN_PRICE = 5;
        int COLUMN_IMAGE_URL = 6;
        int COLUMN_SHORT_DESCRIPTION = 7;
        int COLUMN_IS_DISH_OF_DAY = 8;
        int COLUMN_DISH_OF_DAY_NUMBER = 9;
        int COLUMN_CATEGORY = 10;
    }

}
