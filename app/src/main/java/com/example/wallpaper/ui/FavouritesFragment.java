package com.example.wallpaper.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wallpaper.adapters.PhotoAdapter;
import com.example.wallpaper.databinding.FragmentFavouritesBinding;
import com.example.wallpaper.models.Photo;
import com.example.wallpaper.utils.RealmController;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    private FragmentFavouritesBinding binding;
    private PhotoAdapter photoAdapter;
    private List<Photo> photoList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.fragmentFavouritesRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        photoAdapter = new PhotoAdapter(getActivity(), photoList);
        binding.fragmentFavouritesRecycler.setAdapter(photoAdapter);
        getPhotos();
        return root;
    }

    private void getPhotos() {
        RealmController realmController = new RealmController();
        photoList.addAll(realmController.getPhotos());
        if (photoList.isEmpty()) {
            binding.fragmentFavouritesTextView.setVisibility(View.VISIBLE);
            binding.fragmentFavouritesRecycler.setVisibility(View.GONE);
        } else {
            photoAdapter.notifyDataSetChanged();
            binding.fragmentFavouritesTextView.setVisibility(View.GONE);
            binding.fragmentFavouritesRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}