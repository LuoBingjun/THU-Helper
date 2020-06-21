package com.example.thu_helper.ui.process;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thu_helper.R;
import com.example.thu_helper.ui.home.Record;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProcessListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProcessListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private CardAdapter cardAdapter;
    private ProcessViewModel mViewModel;

    public ProcessListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ProcessListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProcessListFragment newInstance(String param1) {
        ProcessListFragment fragment = new ProcessListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_process_list, container, false);

        mViewModel = ViewModelProviders.of(getActivity()).get(ProcessViewModel.class);

        // Init RecyclerViewPublish
        RecyclerView recyclerView = root.findViewById(R.id.recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        switch(mParam1){
            case "publish":
                mViewModel.getDataPublish().observe(this, new Observer<List<Record>>() {
                    @Override
                    public void onChanged(List<Record> records) {
                        cardAdapter = new CardAdapter(records);
                        recyclerView.setAdapter(cardAdapter);
                    }
                });

                break;
            case "accept":
                mViewModel.getDataAccept().observe(this, new Observer<List<Record>>() {
                    @Override
                    public void onChanged(List<Record> records) {
                        cardAdapter = new CardAdapter(records);
                        recyclerView.setAdapter(cardAdapter);
                    }
                });
                break;
        }

        return root;
    }
}