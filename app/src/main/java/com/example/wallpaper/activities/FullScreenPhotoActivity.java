package com.example.wallpaper.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.wallpaper.R;
import com.example.wallpaper.databinding.ActivityFullScreenPhotoBinding;
import com.example.wallpaper.models.Photo;
import com.example.wallpaper.utils.Functions;
import com.example.wallpaper.utils.RealmController;
import com.example.wallpaper.webservices.ApiInterface;
import com.example.wallpaper.webservices.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullScreenPhotoActivity extends AppCompatActivity {

    private final String TAG = FullScreenPhotoActivity.class.getSimpleName();
    private ActivityFullScreenPhotoBinding binding;

    private Bitmap photoBitmap;

    private RealmController realmController;
    private Photo photo = new Photo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullScreenPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        String photoId = intent.getStringExtra("photoId");
        getPhotoById(photoId);

        realmController = new RealmController();
        if (realmController.isPhotoExist(photoId)) {
            binding.activityFullScreenFabFavourites.setImageResource(R.drawable.ic_fav_red);
        }

        binding.activityFullScreenFabFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFabFavourite();
                binding.activityFullScreenFloatingActionMenu.close(true);
            }
        });

        binding.activityFullScreenFabWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWallpaper();
                binding.activityFullScreenFloatingActionMenu.close(true);

            }
        });
    }

    private void setFabFavourite() {
        if (realmController.isPhotoExist(photo.getId())) {
            realmController.deletePhoto(photo);
            binding.activityFullScreenFabFavourites.setImageResource(R.drawable.ic_fav);
            Toast.makeText(FullScreenPhotoActivity.this, "Removed from favourites" , Toast.LENGTH_SHORT).show();
        } else {
            realmController.savePhoto(photo);
            binding.activityFullScreenFabFavourites.setImageResource(R.drawable.ic_fav_red);
            Toast.makeText(FullScreenPhotoActivity.this, "Added to Favourites", Toast.LENGTH_SHORT).show();
        }
    }

    private void setWallpaper() {
        if (photoBitmap != null) {
            if (Functions.setWallpaper(FullScreenPhotoActivity.this, photoBitmap)) {
                Toast.makeText(FullScreenPhotoActivity.this, "Wallpaper set successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FullScreenPhotoActivity.this, "Set Wallpaper Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getPhotoById(String photoId) {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<Photo> call = apiInterface.getPhotoById(photoId);
        call.enqueue(new Callback<Photo>() {
            @Override
            public void onResponse(Call<Photo> call, Response<Photo> response) {
                if (response.isSuccessful()) {
                    photo = response.body();
                    updateUi(photo);
                } else {
                    Toast.makeText(FullScreenPhotoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Photo> call, Throwable t) {
                Toast.makeText(FullScreenPhotoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "fail "+t.getMessage());
            }
        });
    }

    private void updateUi(Photo photo) {
        try {
            binding.activityFullScreenUsernameTextView.setText(photo.getUser().getUsername());
            Glide
                    .with(FullScreenPhotoActivity.this)
                    .load(photo.getUser().getProfileImage().getSmall())
                    .into(binding.activityFullScreenCircleImageView);

//            using bitmap to add wallpaper
            Glide
                    .with(FullScreenPhotoActivity.this)
                    .asBitmap()
                    .load(photo.getUrl().getRegular())
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            binding.activityFullScreenPhoto.setImageBitmap(resource);
                            photoBitmap = resource;
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}