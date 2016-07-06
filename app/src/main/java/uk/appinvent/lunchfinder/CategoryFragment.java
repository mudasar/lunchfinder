package uk.appinvent.lunchfinder;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.appinvent.lunchfinder.data.DishLoader;
import uk.appinvent.lunchfinder.data.LunchContract;
import uk.appinvent.lunchfinder.ui.RecyclerViewAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CategoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private final String LOG_TAG = CategoryFragment.class.getSimpleName();
    public final static String CATEGORY_URI = "URI";
    public final static String DETAIL_TRANSITION_ANIMATION = "DTA";

    RecyclerViewAdapter mRecyclerViewAdapter;
    RecyclerView mRecyclerView;

    private Uri mUri;

    private static final int DISHES_LOADER = 0;

    private OnFragmentInteractionListener mListener;

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(CategoryFragment.CATEGORY_URI);
            Log.d(LOG_TAG, mUri.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.dish_recycler_view);


        //TODO: put column size in strings
        //Create strings for each different screen size
        //the column size will get modified according to screen size   for small screen it 1 for large its 2

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
        if ( null != mUri ) {

            String category = LunchContract.DishEntry.getCategoryFromUri(mUri);
            DishLoader dishLoader = DishLoader.allDishesByCategoryInstance(getActivity(),category);
            return dishLoader;
        }
        return  null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
      //  dumpCursor(data);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
