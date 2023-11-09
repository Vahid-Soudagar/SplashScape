package com.example.wallpaper.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wallpaper.R;
import com.example.wallpaper.activities.FullScreenPhotoActivity;
import com.example.wallpaper.models.Photo;
import com.example.wallpaper.ui.PhotosFragment;
import com.example.wallpaper.utils.SquareImageView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public Context context;
    public List<Photo> photoList;

    private final String TAG = PhotosFragment.class.getSimpleName();

    private OnItemClickListener onClickListener;

    public PhotoAdapter(Context context, List<Photo> photoList, OnItemClickListener onClickListener) {
        this.context = context;
        this.photoList = photoList;
        this.onClickListener = onClickListener;
    }

    public PhotoAdapter(Context context, List<Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_photos, parent, false)
                .getRootView();
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo currentPhoto = photoList.get(position);
        Log.d(TAG, currentPhoto.toString());
        holder.textView.setText(currentPhoto.getUser().getUsername());
        Glide
        .with(context)
            .load(currentPhoto.getUrl().getRegular())
            .placeholder(R.drawable.ic_menu_camera)
            .override(1000, 1000)
            .into(holder.squareImageView);
        Glide.with(context)
                .load(currentPhoto.getUser().getProfileImage().getSmall())
                .into(holder.circleImageView);

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int p = holder.getAdapterPosition();
                String photoId = currentPhoto.getId();
                Intent intent = new Intent(context, FullScreenPhotoActivity.class);
                intent.putExtra("photoId", photoId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, photoList.size()+"");
        return photoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView circleImageView;
        public SquareImageView squareImageView;
        public TextView textView;
        public FrameLayout frameLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.item_photos_circle_image_view);
            squareImageView = itemView.findViewById(R.id.item_photos_square_image_view);
            textView = itemView.findViewById(R.id.item_photos_text_view);
            frameLayout = itemView.findViewById(R.id.item_photo_layout);
        }
    }
}
