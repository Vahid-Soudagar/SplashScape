/*
    The FullScreenPhotoActivity class represents the activity displaying a photo in fullscreen.
    It uses the Android Navigation component to navigate to this activity with a specific photoId.
    The UI is updated with details fetched from the API, and users can interact by adding/removing
    photos from favorites or setting the photo as wallpaper.
    Glide library is used for efficient image loading and manipulation.
    Error handling and logging are included for API calls and UI updates.
 */
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

    // Tag for logging
    private final String TAG = FullScreenPhotoActivity.class.getSimpleName();

    // View binding for the activity
    private ActivityFullScreenPhotoBinding binding;

    // Bitmap for the photo
    private Bitmap photoBitmap;

    // Controller for Realm database operations
    private RealmController realmController;

    // Photo object to store photo details
    private Photo photo = new Photo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivityFullScreenPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the activity to fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Retrieve photoId from the intent
        Intent intent = getIntent();
        String photoId = intent.getStringExtra("photoId");

        // Retrieve photo details from the API
        getPhotoById(photoId);

        // Initialize Realm controller
        realmController = new RealmController();

        // Update the favorite button based on photo existence in the database
        if (realmController.isPhotoExist(photoId)) {
            binding.activityFullScreenFabFavourites.setImageResource(R.drawable.ic_fav_red);
        }

        // Set onClickListener for the favorite button
        binding.activityFullScreenFabFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle favorite button click
                setFabFavourite();
                binding.activityFullScreenFloatingActionMenu.close(true);
            }
        });

        // Set onClickListener for the wallpaper button
        binding.activityFullScreenFabWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle wallpaper button click
                setWallpaper();
                binding.activityFullScreenFloatingActionMenu.close(true);
            }
        });
    }

    // Method to handle favorite button click
    private void setFabFavourite() {
        if (realmController.isPhotoExist(photo.getId())) {
            // If photo exists in favorites, remove it
            realmController.deletePhoto(photo);
            binding.activityFullScreenFabFavourites.setImageResource(R.drawable.ic_fav);
            Toast.makeText(FullScreenPhotoActivity.this, "Removed from favourites", Toast.LENGTH_SHORT).show();
        } else {
            // If photo doesn't exist in favorites, add it
            realmController.savePhoto(photo);
            binding.activityFullScreenFabFavourites.setImageResource(R.drawable.ic_fav_red);
            Toast.makeText(FullScreenPhotoActivity.this, "Added to Favourites", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to handle wallpaper button click
    private void setWallpaper() {
        if (photoBitmap != null) {
            // If a valid bitmap is available, set it as wallpaper
            if (Functions.setWallpaper(FullScreenPhotoActivity.this, photoBitmap)) {
                Toast.makeText(FullScreenPhotoActivity.this, "Wallpaper set successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FullScreenPhotoActivity.this, "Set Wallpaper Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to retrieve photo details from the API
    private void getPhotoById(String photoId) {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<Photo> call = apiInterface.getPhotoById(photoId);
        call.enqueue(new Callback<Photo>() {
            @Override
            public void onResponse(Call<Photo> call, Response<Photo> response) {
                if (response.isSuccessful()) {
                    // If the API call is successful, update the UI
                    photo = response.body();
                    updateUi(photo);
                } else {
                    // Display a toast if something went wrong with the API call
                    Toast.makeText(FullScreenPhotoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Photo> call, Throwable t) {
                // Display a toast if the API call fails, along with logging the error
                Toast.makeText(FullScreenPhotoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "fail " + t.getMessage());
            }
        });
    }

    // Method to update the UI with photo details
    private void updateUi(Photo photo) {
        try {
            // Set the username in the UI
            binding.activityFullScreenUsernameTextView.setText(photo.getUser().getUsername());

            // Load the user's profile image using Glide
            Glide
                    .with(FullScreenPhotoActivity.this)
                    .load(photo.getUser().getProfileImage().getSmall())
                    .into(binding.activityFullScreenCircleImageView);

            // Load the photo using Glide as a bitmap for wallpaper
            Glide
                    .with(FullScreenPhotoActivity.this)
                    .asBitmap()
                    .load(photo.getUrl().getRegular())
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Set the bitmap in the UI and store it for wallpaper setting
                            binding.activityFullScreenPhoto.setImageBitmap(resource);
                            photoBitmap = resource;
                        }
                    });

        } catch (Exception e) {
            // Handle any exceptions that may occur during UI update
            e.printStackTrace();
        }
    }
}
