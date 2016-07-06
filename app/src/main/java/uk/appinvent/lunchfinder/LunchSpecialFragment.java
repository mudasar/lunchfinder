package uk.appinvent.lunchfinder;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import uk.appinvent.lunchfinder.data.DishLoader;
import uk.appinvent.lunchfinder.data.LunchContract;
import uk.appinvent.lunchfinder.ui.RecyclerViewAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class LunchSpecialFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private final String LOG_TAG = LunchSpecialFragment.class.getSimpleName();

    RecyclerViewAdapter mRecyclerViewAdapter;
    RecyclerView mRecyclerView;

    private static final int DISHES_LOADER = 0;

    public LunchSpecialFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.dish_recycler_view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),Integer.parseInt(getString(R.string.column_size))));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerViewAdapter = new RecyclerViewAdapter(getActivity(), new RecyclerViewAdapter.RecyclerViewAdapterOnClickHandler() {
            @Override
            public void onClick(Long id, RecyclerViewAdapter.CardViewHolder vh) {
                ((Callback) getActivity())
                        .onItemSelected(LunchContract.DishEntry.buildDishUri(id), vh);
            }
        });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        return  rootView;
    }


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dishUri, RecyclerViewAdapter.CardViewHolder vh);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DISHES_LOADER,null, this);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Calendar calendar = Calendar.getInstance();
        int day =  calendar.get(Calendar.DAY_OF_WEEK);

        return DishLoader.allSpecialDishesInstance(getActivity(), day);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecyclerViewAdapter.swapCurson(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mRecyclerView) {
            mRecyclerView.clearOnScrollListeners();
        }
    }
}
