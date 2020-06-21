package com.example.thu_helper.ui.home;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thu_helper.R;
import com.example.thu_helper.data.LoginRepository;
import com.example.thu_helper.data.Result;
import com.example.thu_helper.data.model.LoggedInUser;
import com.example.thu_helper.ui.new_order.NewOrderActivity;
import com.example.thu_helper.utils.Global;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private LoggedInUser loggedInUser;

    private HomeViewModel homeViewModel;
    private View mFakeStatusBar;
    private CardAdapter cardAdapter;
    private NiceSpinner mNiceSpinner;
    private SearchView mSearchView;
    private FloatingActionButton mAddButton;

    private LiveData<String> mWord;
    private LiveData<String> mTime;

    @Override
    public void onResume() {
        super.onResume();
        new GetTask().execute(mWord.getValue(), mTime.getValue());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loggedInUser = LoginRepository.getInstance().getUser();

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        mWord = homeViewModel.getWord();
        mTime = homeViewModel.getTime();

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Set height of fake_status_bar
        mFakeStatusBar = root.findViewById(R.id.fake_status_bar);
        Resources resources = getContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int statusHeight = resources.getDimensionPixelSize(resourceId);
        ConstraintLayout.LayoutParams params=new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, statusHeight);
        mFakeStatusBar.setLayoutParams(params);

        // Init Spinner
        mNiceSpinner = root.findViewById(R.id.nice_spinner);
        List<String> dataset = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.times)));
        mNiceSpinner.attachDataSource(dataset);
        mNiceSpinner.setOnSpinnerItemSelectedListener(onSpinnerSelected);

        // Init SearchView
        mSearchView = root.findViewById(R.id.searchView);
        mSearchView.setOnQueryTextListener(onQueryTextListener);


        // Init RecyclerView
        cardAdapter = new CardAdapter(homeViewModel.getData());
        RecyclerView recyclerView = root.findViewById(R.id.recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cardAdapter);

        mWord.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                new GetTask().execute(s, mTime.getValue());
            }
        });

        mTime.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                new GetTask().execute(mWord.getValue(), s);
            }
        });

        // Init AddButton
        mAddButton = root.findViewById(R.id.floatingActionButton);
        mAddButton.setOnClickListener(onAddButtonClicked);
        return root;
    }

    private View.OnClickListener onAddButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), NewOrderActivity.class);
            startActivity(intent);
        }
    };

    private OnSpinnerItemSelectedListener onSpinnerSelected = new OnSpinnerItemSelectedListener() {
        @Override
        public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
            String item = (String) parent.getItemAtPosition(position);
            homeViewModel.setTime(item);
        }
    };

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            homeViewModel.setWord(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            homeViewModel.setWord(newText);
            return false;
        }
    };

    private class GetTask extends AsyncTask<String, Integer, Result<List<Record>>> {

        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作（UI线程）
        @Override
        protected void onPreExecute() {
//            mProgressBar.setVisibility(View.VISIBLE);
        }

        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果（子线程）
        // 此处通过计算从而模拟“加载进度”的情况
        @Override
        protected Result<List<Record>> doInBackground(String... params) {
            try {
                String paramWord = params[0];
                String paramTime = params[1];

                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder urlBuilder =HttpUrl.parse(Global.url_prefix + "/activity/list")
                        .newBuilder();
                if(paramWord != null){
                    urlBuilder.addQueryParameter("word", paramWord);
                }
                if(paramTime != null){
                    urlBuilder.addQueryParameter("time", paramTime);
                }
                Request request = new Request.Builder()
                        .url(urlBuilder.build())
                        .addHeader("Authorization","Token " + loggedInUser.token)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONArray res = new JSONArray(response.body().string());
                    List<Record> records = parseRecords(res);
                    return new Result.Success<>(records);
                }
                return new Result.Error(new Exception("请求失败，请联系网站管理员"));
            } catch (Exception e) {
                return new Result.Error(new Exception("网络请求失败，请稍后重试", e));
            }
        }

        private List<Record> parseRecords(JSONArray res) throws JSONException, ParseException {
            ArrayList<Record> records = new ArrayList<>();

            for(int i = 0; i < res.length(); i++){
                JSONObject obj = res.getJSONObject(i);
                Record record = new Record(obj);
                records.add(record);
            }

            return records;
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度（UI线程）
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件（UI线程）
        @Override
        protected void onPostExecute(Result<List<Record>> result) {
            if (result instanceof Result.Success) {
                homeViewModel.getData().clear();
                homeViewModel.getData().addAll(((Result.Success<List<Record>>) result).getData());
                cardAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), ((Result.Error) result).getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        // 方法5：onCancelled()
        // 作用：将异步任务设置为：取消状态（UI线程）
        @Override
        protected void onCancelled() {
//            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

}