package uk.appinvent.lunchfinder;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import uk.appinvent.lunchfinder.data.Dish;
import uk.appinvent.lunchfinder.data.DishLoader;
import uk.appinvent.lunchfinder.data.LunchContract;
import uk.appinvent.lunchfinder.data.Order;
import uk.appinvent.lunchfinder.data.User;


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

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseDatabase mDatabase ;
    DatabaseReference mDatabaseRef;

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
    private User user;

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
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        try {
            // First, check if the location with this city name exists in the db
            Cursor userCursor =getActivity().getContentResolver().query(
                    LunchContract.UserEntry.CONTENT_URI,
                    DishLoader.UserQuery.PROJECTION,
                    null,
                    null,
                    null);

            if(userCursor != null)
            {
                if (userCursor.moveToFirst()) {
                    user = User.fromCursor(userCursor);
                }
            }
        }catch (Exception e){
            //TODO: remave strack trace when realese
            e.printStackTrace();
            Log.d(LOG_TAG,e.getMessage());
        }
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // User is signed in
                    user.setUid(firebaseUser.getUid());
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                } else {
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
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



                String key = mDatabaseRef.child("orders").push().getKey();
                Order order = new Order(1, dish.getRefId(), dish.getName(), dish.getImageUrl(), 1,"",
                        user.getName(), "new", user.getUid());
                Map<String, Object> postValues = order.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/orders/" + key, postValues);
                childUpdates.put("/user-orders/" + user.getUid() + "/" + key, postValues);

                mDatabaseRef.updateChildren(childUpdates);


                //TODO: open order activity
                Toast.makeText(getActivity(), "Your order is placed now", Toast.LENGTH_SHORT).show();

                //redirect user to main activity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.share_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(dish.getId());
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
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {

            Integer dishId = LunchContract.DishEntry.getDishIdFromUri(mUri);
            DishLoader dishLoader = DishLoader.newInstanceForDishId(getActivity(),dishId);
            return dishLoader;
        }

//        ViewParent vp = getView().getParent();
//        if ( vp instanceof CardView) {
//            ((View)vp).setVisibility(View.INVISIBLE);
//        }
        return null;
    }

    private void share(long id) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        String url = "lunchfinder://uk.appinvent/dish/"+id;
        share.putExtra(Intent.EXTRA_SUBJECT, "Lunchfinder share");
        share.putExtra(Intent.EXTRA_TEXT, url);
        share.putExtra(Intent.EXTRA_HTML_TEXT, "<a href='"+url+"'>Lunch Finder</a>");
       Log.d(LOG_TAG, share.toUri(Intent.URI_INTENT_SCHEME).toString());
        startActivity(Intent.createChooser(share, "Share a Meal!"));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    //TODO: link the dish details to view items
        if (data != null && data.moveToFirst()) {
            dish = new Dish();
            dish.setId(data.getLong( DishLoader.Query._ID));
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
