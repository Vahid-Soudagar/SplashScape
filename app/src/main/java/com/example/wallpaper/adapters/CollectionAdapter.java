package com.example.wallpaper.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wallpaper.R;
import com.example.wallpaper.models.Collection;
import com.example.wallpaper.utils.SquareImageView;
import java.util.List;

public class CollectionAdapter extends BaseAdapter {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener onClickListener;
    private final String TAG = CollectionAdapter.class.getSimpleName();
    private Context context;
    private List<Collection>  collectionList;

    public CollectionAdapter(Context context, List<Collection> collectionList, OnItemClickListener onClickListener) {
        this.context = context;
        this.collectionList = collectionList;
        this.onClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return collectionList.size();
    }

    @Override
    public Object getItem(int i) {
        return collectionList.get(i);
    }

    @Override
    public long getItemId(int i) {
        Log.d(TAG, "Error in getItemId");
        return Long.parseLong(collectionList.get(i).getId());
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_collection, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Collection currentCollection = collectionList.get(i);
        Log.d(TAG, currentCollection.toString());
        if (currentCollection.getTitle() != null) {
            Log.d("Title", currentCollection.getTitle());
            viewHolder.titleTextView.setText(currentCollection.getTitle());
        }
        viewHolder.noOfPhotosTextView.setText(currentCollection.getTotalPhotos()+" Photos");
        Glide
                .with(context)
                .load(currentCollection.getCoverPhoto().getUrl().getRegular())
                .override(400, 400)
                .into(viewHolder.squareImageView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onItemClick(i);
                }
            }
        });

        return view;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public SquareImageView squareImageView;
        public TextView titleTextView, noOfPhotosTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            squareImageView = itemView.findViewById(R.id.item_collection_square_image_view);
            titleTextView = itemView.findViewById(R.id.item_collection_title_text_view);
            noOfPhotosTextView = itemView.findViewById(R.id.item_collection_no_of_photos_text_view);
        }
    }


}
