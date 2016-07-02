package uk.appinvent.lunchfinder;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import uk.appinvent.lunchfinder.data.Dish;
import uk.appinvent.lunchfinder.data.LunchContract;
import uk.appinvent.lunchfinder.data.User;
import uk.appinvent.lunchfinder.ui.RecyclerViewAdapter;

public class MainActivity extends AppCompatActivity implements LunchSpecialFragment.Callback {


    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String CATEGORYFRAGMENT_TAG = "CFTAG";
     private User user;
    private List<Dish> dishList;

    //Drawer layout
    private String[] mCategoryList;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private Handler mHandler = new Handler();

    private static final int DRAWER_CLOSE_DELAY = 250;

    private FragmentTransaction ft;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO Check if user is configured   if not redirect ot signup activity
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean sentToken = sharedPreferences.getBoolean(getString(R.string.is_user_registered), false);
        if (!sentToken) {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        } else {
            // Find the first person (no query conditions) and read a field

            try {
                // First, check if the location with this city name exists in the db
                Cursor userCursor = getApplicationContext().getContentResolver().query(
                        LunchContract.UserEntry.CONTENT_URI,
                        new String[]{"*"},
                        null,
                        null,
                        null);

                if(userCursor != null)
                {
                    if (userCursor.moveToFirst()) {
                        User user = User.fromCursor(userCursor);
                        String user_string = user.getName() + ":" + user.getPhone();

                        Toast.makeText(getApplicationContext(), user_string, Toast.LENGTH_SHORT).show();

                        TextView welcome_view = (TextView) findViewById(R.id.welcome_text);
                        String formated_name = String.format(getString(R.string.welcome_msg), user.getName());
                        welcome_view.setText(formated_name);
                    }
                }
            }catch (Exception e){
                //TODO: remave strack trace when realese
                e.printStackTrace();
                Log.d(LOG_TAG,e.getMessage());
            }

            ft = getFragmentManager().beginTransaction();

        }

        mActivityTitle = getTitle().toString();
        mCategoryList = getResources().getStringArray(R.array.category_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        setupDrawer(toolbar);

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.nav_curry:
                                loadCategory("Curry");
                                return true;
                            case R.id.nav_pasta:
                                loadCategory("Pasta");
                                return true;

                            case R.id.nav_salad:
                                loadCategory("Salad");
                                return true;
                            case R.id.nav_sandwich:
                                loadCategory("Sandwich");
                                return true;

                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

    }

    private void setupDrawer(Toolbar toolbar) {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
//
//
//        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
         mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;


            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadCategory(String category) {

            if (mDrawerLayout.isDrawerOpen(mNavigationView))
                mDrawerLayout.closeDrawer(mNavigationView);

        Uri uri =  LunchContract.DishEntry.buildCategoryUri(category);
        Intent intent = new Intent(this, RegularMenuActivity.class)
                .setData(uri);

        startActivity(intent);
    }

    @Override
    public void onItemSelected(Uri dishUri, RecyclerViewAdapter.CardViewHolder vh) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DishDetailFragment.DETAIL_URI, dishUri);

            DishDetailFragment fragment = new DishDetailFragment();
            fragment.setArguments(args);

            getFragmentManager().beginTransaction().replace(R.id.dish_detail_container, fragment, DETAILFRAGMENT_TAG).commit();

        } else {
            Intent intent = new Intent(this, DishDetailsActivity.class)
                    .setData(dishUri);

            startActivity(intent);
        }
    }


}
