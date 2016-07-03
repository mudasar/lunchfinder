package uk.appinvent.lunchfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import uk.appinvent.lunchfinder.data.LunchContract;

public class DishDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle arguments = new Bundle();

        //Printing detail of clicked item from widget
        Intent intent = getIntent();
        Uri data = intent.getData();
        if ( intent != null && intent.getType() != null &&  intent.getType().equals("text/plain")){
            long id = Long.parseLong(data.getLastPathSegment());
            Uri dishUri = LunchContract.DishEntry.buildDishUri(id);
            arguments.putParcelable(DishDetailFragment.DETAIL_URI, dishUri);
        }else{
            arguments.putParcelable(DishDetailFragment.DETAIL_URI, getIntent().getData());

        }
//        if (intent !=null) {
//            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                    AppWidgetManager.INVALID_APPWIDGET_ID);
//
//            long dishId = intent.getLongExtra(SpecialWidget.EXTRA_ID, 1);
//
//            Toast.makeText(getApplicationContext(), "Touched view " + dishId, Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(getApplicationContext(), "Touched view none ", Toast.LENGTH_SHORT).show();
//        }


        arguments.putBoolean(DishDetailFragment.DETAIL_TRANSITION_ANIMATION, true);
        DishDetailFragment fragment = new DishDetailFragment();
        fragment.setArguments(arguments);

        getFragmentManager().beginTransaction().add(R.id.dish_detail_container, fragment).commit();

        // Being here means we are in animation mode
        supportPostponeEnterTransition();
    }

}
