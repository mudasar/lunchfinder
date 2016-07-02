package uk.appinvent.lunchfinder.widget;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import uk.appinvent.lunchfinder.R;
import uk.appinvent.lunchfinder.data.Dish;
import uk.appinvent.lunchfinder.data.DishLoader;
import uk.appinvent.lunchfinder.data.LunchContract;

/**
 * Created by mudasar on 02/07/16.
 */
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();

    static final int INDEX_DISH_ID = 0;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(LOG_TAG, "Service is called");
        return new RemoteViewsFactory() {

            private Cursor data = null;

            @Override
            public void onCreate() {
                Log.d(LOG_TAG, "Service is called");
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                Calendar calendar = Calendar.getInstance();
                int day =  calendar.get(Calendar.DAY_OF_WEEK);
                Uri specialUri = LunchContract.DishEntry.buildSpecialUri(day);

                data = getContentResolver().query(specialUri,
                        DishLoader.Query.PROJECTION,
                        null,
                        null,
                        LunchContract.DishEntry.DEFAULT_SORT + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);

              Dish dish = new Dish();
                dish.setRefId(data.getLong(DishLoader.Query.COLUMN_REF_ID));
                dish.setName(data.getString(DishLoader.Query.COLUMN_NAME));
                dish.setPrice(data.getDouble(DishLoader.Query.COLUMN_PRICE));
                dish.setImageUrl(data.getString(DishLoader.Query.COLUMN_IMAGE_URL));


                views.setTextViewText(R.id.dish_name, dish.getName());
                NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
                views.setTextViewText(R.id.dish_price, String.format("%s", defaultFormat.format(dish.getPrice())));
                Bitmap dishImage = null;
                try {
                    dishImage = Glide.with(DetailWidgetRemoteViewsService.this)
                            .load(dish.getImageUrl())
                            .asBitmap()
                            .error(R.id.dish_photo)
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(LOG_TAG, "Error retrieving large icon from " + dish.getImageUrl(), e);
                }

                final Intent fillInIntent = new Intent();

                Uri dishUri = LunchContract.DishEntry.buildDishUri(data.getLong(DishLoader.Query._ID));
                fillInIntent.setData(dishUri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_DISH_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }


}
