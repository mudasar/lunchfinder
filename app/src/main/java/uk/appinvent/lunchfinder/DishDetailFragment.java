package uk.appinvent.lunchfinder;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.NumberFormat;

import uk.appinvent.lunchfinder.data.Dish;
import uk.appinvent.lunchfinder.data.DishLoader;
import uk.appinvent.lunchfinder.data.LunchContract;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DishDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DishDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DishDetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";
    static final String DETAIL_TRANSITION_ANIMATION = "DTA";

    private static final String LUNCH_FINDER_SHARE_HASHTAG = " #LunchFinder";
    private Uri mUri;
    private boolean mTransitionAnimation;

    private static final int DETAIL_LOADER = 0;

    private Dish dish;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView photo;
    private TextView name;
    private TextView description;
    private TextView short_description;
    private TextView price;
    private Button order_now;


    public DishDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DishDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DishDetailFragment newInstance(String param1, String param2) {
        DishDetailFragment fragment = new DishDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DishDetailFragment.DETAIL_URI);
            Log.d(LOG_TAG, mUri.toString());
            mTransitionAnimation = arguments.getBoolean(DishDetailFragment.DETAIL_TRANSITION_ANIMATION, false);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dish_detail, container, false);

        name = (TextView) view.findViewById(R.id.name_text);
        description = (TextView) view.findViewById(R.id.description);
        short_description = (TextView) view.findViewById(R.id.short_description_text);
        price = (TextView) view.findViewById(R.id.price);
        order_now = (Button) view.findViewById(R.id.order_btn);

        order_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: open order activity
                Toast.makeText(getActivity(), "Order now", Toast.LENGTH_SHORT).show();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.share_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

        photo = (ImageView) view.findViewById(R.id.photo);

        getLoaderManager().initLoader(DETAIL_LOADER,null, this);
        if(dish != null) {
            ImageLoader.getInstance().displayImage(dish.getImageUrl(), photo);
        }
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {

            Integer dishId = LunchContract.DishEntry.getDishIdFromUri(mUri);
            DishLoader dishLoader = DishLoader.newInstanceForDishId(getActivity(),dishId);
            return dishLoader;
        }
        ViewParent vp = getView().getParent();
        if ( vp instanceof CardView) {
            ((View)vp).setVisibility(View.INVISIBLE);
        }
        return null;
    }

    private void share() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Lunchfinder share");
        share.putExtra(Intent.EXTRA_TEXT, "http://");

        startActivity(Intent.createChooser(share, "Share a Meal!"));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    //TODO: link the dish details to view items
        if (data != null && data.moveToFirst()) {
            dish = new Dish();
            dish.setRefId(data.getLong(DishLoader.Query.COLUMN_REF_ID));
            dish.setName(data.getString(DishLoader.Query.COLUMN_NAME));
            dish.setDescription(data.getString(DishLoader.Query.COLUMN_DESCRIPTION));
            dish.setWaitingTime(data.getInt(DishLoader.Query.COLUMN_WAITING_TIME));
            dish.setPrice(data.getDouble(DishLoader.Query.COLUMN_PRICE));
            dish.setShortDescription(data.getString(DishLoader.Query.COLUMN_SHORT_DESCRIPTION));
            dish.setCategory(data.getString(DishLoader.Query.COLUMN_CATEGORY));
            dish.setImageUrl(data.getString(DishLoader.Query.COLUMN_IMAGE_URL));
            dish.setDishOftheDay(Boolean.parseBoolean(data.getString(DishLoader.Query.COLUMN_IS_DISH_OF_DAY)));
            dish.setDishOfDayNumber(data.getInt(DishLoader.Query.COLUMN_DISH_OF_DAY_NUMBER));



            //TODO: Link the views
            name.setText(dish.getName());
            description.setText(dish.getDescription());
            short_description.setText(dish.getShortDescription());

            NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
            price.setText(String.format("%s", defaultFormat.format(dish.getPrice())));


            ImageLoader.getInstance().displayImage(dish.getImageUrl(), photo);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
