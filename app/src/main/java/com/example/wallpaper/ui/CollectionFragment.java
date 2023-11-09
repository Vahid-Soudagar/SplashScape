package com.example.wallpaper.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wallpaper.adapters.PhotoAdapter;
import com.example.wallpaper.databinding.FragmentCollectionBinding;
import com.example.wallpaper.models.Collection;
import com.example.wallpaper.models.Photo;
import com.example.wallpaper.webservices.ApiInterface;
import com.example.wallpaper.webservices.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionFragment extends Fragment {

    private final String TAG = CollectionFragment.class.getSimpleName();
    private FragmentCollectionBinding binding;

    private List<Photo> photoList = new ArrayList<>();
    private PhotoAdapter photoAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCollectionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.fragmentCollectionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        photoAdapter = new PhotoAdapter(getActivity(), photoList);
        binding.fragmentCollectionsRecyclerView.setAdapter(photoAdapter);

        Bundle bundle = getArguments();
        String collectionId = bundle.getString("collectionId");
        Toast.makeText(getContext(), collectionId+"", Toast.LENGTH_SHORT).show();
        Log.e(TAG, collectionId+"");
        setProgressBar(true);
        getInformationOfCollection(Integer.parseInt(collectionId));
        getPhotosOfCollection(Integer.parseInt(collectionId));
        return root;
    }

    private void getInformationOfCollection(int collectionId) {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<Collection> call = apiInterface.getInformationOfCollection(collectionId);
        call.enqueue(new Callback<Collection>() {
            @Override
            public void onResponse(Call<Collection> call, Response<Collection> response) {
                if (response.isSuccessful()) {
                    Collection currentCollection = response.body();
                    binding.fragmentCollectionTitle.setText(currentCollection.getTitle());
                    binding.fragmentCollectionDescription.setText(currentCollection.getDescription());
                    binding.fragmentCollectionUserNameTextView.setText(currentCollection.getUser().getUsername());
                    Glide
                            .with(getActivity())
                            .load(currentCollection.getUser().getProfileImage().getSmall())
                            .into(binding.fragmentCollectionCircleImageView);
                } else {
                    Log.e(TAG, "Fail "+response.message()+"" +
                            " "+response.body());
                }
                setProgressBar(false);

            }

            @Override
            public void onFailure(Call<Collection> call, Throwable t) {
                Log.e(TAG, "Fail "+t.getMessage());
                setProgressBar(false);

            }
        });
    }

   private void getPhotosOfCollection(int collectionId) {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<List<Photo>> call = apiInterface.getPhotosOfCollection(collectionId);
        call.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                if (response.isSuccessful()) {
                    photoList.addAll(response.body());
                    photoAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Fail "+response.message()+"" +
                            " "+response.body()+"" +
                            " "+response.errorBody());
                }
                setProgressBar(false);
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                Log.d(TAG, "Fail "+ t.getMessage());
                setProgressBar(false);
            }
        });
   }

    private void setProgressBar(boolean isShow){
        if (isShow) {
            Log.d(TAG, "progress is showing");
            binding.fragmentCollectionProgressBar.setVisibility(View.VISIBLE);
            binding.fragmentCollectionsRecyclerView.setVisibility(View.GONE);
        } else {
            binding.fragmentCollectionProgressBar.setVisibility(View.GONE);
            binding.fragmentCollectionsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}