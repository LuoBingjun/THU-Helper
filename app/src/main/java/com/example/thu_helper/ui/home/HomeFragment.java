package com.example.thu_helper.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thu_helper.ui.detail.DetailActivity;
import com.example.thu_helper.R;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        List<Record> records = homeViewModel.getData().getValue();

        // Init RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new CardAdapter(records));
        return root;
    }

}