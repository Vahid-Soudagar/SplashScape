package com.example.wallpaper.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallpaper.adapters.PhotoAdapter;
import com.example.wallpaper.databinding.FragmentPhotosBinding;
import com.example.wallpaper.models.Photo;
import com.example.wallpaper.webservices.ApiInterface;
import com.example.wallpaper.webservices.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotosFragment extends Fragment {

    private FragmentPhotosBinding binding;
    private final String TAG = PhotosFragment.class.getSimpleName();
    private PhotoAdapter photoAdapter;
    private List<Photo> photoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPhotosBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = binding.fragmentPhotosRecyclerView;
        progressBar = binding.fragmentPhotosProgressBar;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        photoAdapter = new PhotoAdapter(getActivity(), photoList);
        recyclerView.setAdapter(photoAdapter);

        setProgressBar(true);
        getPhotos();

        return view;
    }

    private void getPhotos() {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<List<Photo>> call = apiInterface.getPhotos();
        call.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                if (response.isSuccessful()) {
                    photoList.addAll(response.body());
                    photoAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "fail " +
                            " "+response.errorBody()+"" +
                            " "+response.message()+" " +
                            " "+response.body());
                }
                setProgressBar(false);
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                Log.d(TAG, "fail "+t.getMessage());
                setProgressBar(false);
            }
        });
    }

    private void setProgressBar(boolean isShow){
        if (isShow) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}