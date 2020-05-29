package com.example.thu_helper.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.thu_helper.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ListView listView = (ListView) root.findViewById(R.id.listView);
        List<Map<String, Object>> list=getData();
        listView.setAdapter(new ListViewAdapter(getActivity(), list));
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    public List<Map<String, Object>> getData(){
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("image", R.drawable.ic_order_128dp);
            map.put("title", "帮忙取快递"+i);
            map.put("loc", "紫荆14号楼后小树林");
            map.put("time", new java.text.SimpleDateFormat("MM-dd hh:mm").format(new Date()));
            map.put("info", "最近不在学校，求帮忙取快递" + i);
            list.add(map);
            map=new HashMap<String, Object>();
            map.put("image", R.drawable.ic_order_128dp);
            map.put("title", "示例订单"+i);
            map.put("loc", "紫荆操场");
            map.put("time", new java.text.SimpleDateFormat("MM-dd hh:mm").format(new Date()));
            map.put("info", "示例订单内容" + i);
            list.add(map);
        }
        return list;
    }
}
