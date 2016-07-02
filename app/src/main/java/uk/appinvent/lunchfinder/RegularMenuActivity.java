package uk.appinvent.lunchfinder;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import uk.appinvent.lunchfinder.data.LunchContract;
import uk.appinvent.lunchfinder.ui.RecyclerViewAdapter;

public class RegularMenuActivity extends AppCompatActivity implements CategoryFragment.OnFragmentInteractionListener, CategoryFragment.Callback {

    private static final String LOG_TAG = RegularMenuActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_menu);
        Bundle arguments = new Bundle();
        arguments.putParcelable(CategoryFragment.CATEGORY_URI, getIntent().getData());
        arguments.putBoolean(CategoryFragment.DETAIL_TRANSITION_ANIMATION, true);

        String title = "Regular Menu";

        if (arguments != null) {
            Uri uri = arguments.getParcelable(CategoryFragment.CATEGORY_URI);
            title = LunchContract.DishEntry.getCategoryFromUri(uri);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(arguments);

        getFragmentManager().beginTransaction().add(R.id.lunch_menu_container, fragment).commit();

        // Being here means we are in animation mode
        supportPostponeEnterTransition();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(LOG_TAG, uri.toString());
    }

    @Override
    public void onItemSelected(Uri dishUri, RecyclerViewAdapter.CardViewHolder vh) {
        Intent intent = new Intent(this, DishDetailsActivity.class)
                .setData(dishUri);

        startActivity(intent);
    }
}
