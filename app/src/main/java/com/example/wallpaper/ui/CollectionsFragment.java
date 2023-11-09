package com.example.wallpaper.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.wallpaper.adapters.CollectionAdapter;
import com.example.wallpaper.databinding.FragmentCollectionBinding;
import com.example.wallpaper.databinding.FragmentCollectionsBinding;
import com.example.wallpaper.models.Collection;
import com.example.wallpaper.utils.Functions;
import com.example.wallpaper.webservices.ApiInterface;
import com.example.wallpaper.webservices.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionsFragment extends Fragment {

    private FragmentCollectionsBinding binding;
    private final String TAG = CollectionsFragment.class.getSimpleName();
    private List<Collection> collectionList = new ArrayList<>();
    private CollectionAdapter collectionAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCollectionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        collectionAdapter = new CollectionAdapter(getActivity(), collectionList, new CollectionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Collection collection = collectionList.get(position);
                Log.d(TAG, collection.getId() + "");
                Bundle bundle = new Bundle();
                if (collection.getId().equals("_hr74f0V1Eg")) {
                    Toast.makeText(getContext(), "Collection not Available", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), collection.getId()+"", Toast.LENGTH_SHORT).show();
                    bundle.putString("collectionId", collection.getId());
                    CollectionFragment collectionFragment = new CollectionFragment();
                    collectionFragment.setArguments(bundle);
                    Functions.changeMainFragment(getActivity(), collectionFragment);
                }
            }
        });

        binding.fragmentCollectionGridView.setAdapter(collectionAdapter);
        setProgressBar(true);
        getCollection();
        return root;
    }
    private void getCollection() {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<List<Collection>> call = apiInterface.getCollections();
        call.enqueue(new Callback<List<Collection>>() {
            @Override
            public void onResponse(Call<List<Collection>> call, Response<List<Collection>> response) {
                if (response.isSuccessful()) {
                    for(Collection collection: response.body()){
                        collectionList.add(collection);
                    }
                    collectionAdapter.notifyDataSetChanged();
                    Log.d(TAG, "size " + collectionList.size());
                } else {
                    Log.d(TAG, "fail " +
                            " "+response.errorBody()+"" +
                            " "+response.message()+" " +
                            " "+response.body());
                }

                setProgressBar(false);
            }

            @Override
            public void onFailure(Call<List<Collection>> call, Throwable t) {
                Log.d(TAG, "fail "+t.getMessage());
                setProgressBar(false);
            }
        });
    }

    private void setProgressBar(boolean isShow){
        if (isShow) {
            Log.d(TAG, "progress is showing");
            binding.fragmentCollectionProgressBar.setVisibility(View.VISIBLE);
            binding.fragmentCollectionGridView.setVisibility(View.GONE);
        } else {
            binding.fragmentCollectionProgressBar.setVisibility(View.GONE);
            binding.fragmentCollectionGridView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}