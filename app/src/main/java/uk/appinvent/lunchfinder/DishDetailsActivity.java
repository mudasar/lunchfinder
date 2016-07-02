package uk.appinvent.lunchfinder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DishDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Bundle arguments = new Bundle();
        arguments.putParcelable(DishDetailFragment.DETAIL_URI, getIntent().getData());
        arguments.putBoolean(DishDetailFragment.DETAIL_TRANSITION_ANIMATION, true);

        DishDetailFragment fragment = new DishDetailFragment();
        fragment.setArguments(arguments);

        getFragmentManager().beginTransaction().add(R.id.dish_detail_container, fragment).commit();

        // Being here means we are in animation mode
        supportPostponeEnterTransition();
    }

}
