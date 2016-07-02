package uk.appinvent.lunchfinder.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import uk.appinvent.lunchfinder.R;
import uk.appinvent.lunchfinder.data.DishLoader;
import uk.appinvent.lunchfinder.data.LunchContract;

/**
 * Created by mudasar on 25/06/16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CardViewHolder> {


    private Context context;
    private Cursor mCursor;
    private RecyclerViewAdapterOnClickHandler mRecyclerViewAdapterOnClickHandler;


    public RecyclerViewAdapter(Context context, RecyclerViewAdapterOnClickHandler mRecyclerViewAdapterOnClickHandler) {
        this.context = context;
        this.mRecyclerViewAdapterOnClickHandler = mRecyclerViewAdapterOnClickHandler;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.special_card_view, parent, false);

        CardViewHolder holder = new CardViewHolder(v);

        return holder;
    }

    public static interface RecyclerViewAdapterOnClickHandler {
        void onClick(Long id, CardViewHolder vh);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView


        //holder.imageView.setImageResource(list.get(position).getImageUrl());
        mCursor.moveToPosition(position);

        holder.title.setText(mCursor.getString(DishLoader.Query.COLUMN_NAME));
        holder.description.setText(mCursor.getString(DishLoader.Query.COLUMN_SHORT_DESCRIPTION));
        holder.price.setText(String.format("£%s",mCursor.getDouble(DishLoader.Query.COLUMN_PRICE)));


        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mCursor.getString(DishLoader.Query.COLUMN_IMAGE_URL), holder.imageView);
        animate(holder);
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.bounce_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCurson(Cursor cursor){
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(DishLoader.Query._ID);
    }


    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cv;
        TextView title;
        TextView description;
        TextView price;
        ImageView imageView;

        public CardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardview);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            imageView = (ImageView) itemView.findViewById(R.id.thumbnail);
            price = (TextView) itemView.findViewById(R.id.price);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int idColumnIndex = mCursor.getColumnIndex(LunchContract.DishEntry._ID);
            mRecyclerViewAdapterOnClickHandler.onClick(mCursor.getLong(idColumnIndex), this);
        }
    }

}
