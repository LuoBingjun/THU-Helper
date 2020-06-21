package com.example.thu_helper.ui.process;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.thu_helper.R;
import com.example.thu_helper.data.LoginRepository;
import com.example.thu_helper.data.Result;
import com.example.thu_helper.data.model.LoggedInUser;
import com.example.thu_helper.ui.home.CardAdapter;
import com.example.thu_helper.ui.home.Record;
import com.example.thu_helper.utils.Global;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProcessFragment extends Fragment {

    private ProcessViewModel mViewModel;
    private View mFakeStatusBar;
    private LoggedInUser loggedInUser;
    private ViewPager mViewPager;

    public static ProcessFragment newInstance() {
        return new ProcessFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetTask().execute();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_process, container, false);
        mViewModel = ViewModelProviders.of(getActivity()).get(ProcessViewModel.class);
        loggedInUser = LoginRepository.getInstance().getUser();

        // Set height of fake_status_bar
        mFakeStatusBar = root.findViewById(R.id.fake_status_bar);
        Resources resources = getContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int statusHeight = resources.getDimensionPixelSize(resourceId);
        ConstraintLayout.LayoutParams params=new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, statusHeight);
        mFakeStatusBar.setLayoutParams(params);

        // Init ViewPager
        mViewPager = root.findViewById(R.id.view_pager);
        FragmentPagerAdapter processPagerAdapter = new ProcessPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(processPagerAdapter);

        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public class ProcessPagerAdapter extends FragmentPagerAdapter {
        ArrayList<ProcessListFragment> fragments;
        String titles[] = {"作为发单方", "作为接单方"};
        public ProcessPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(ProcessListFragment.newInstance("publish"));
            fragments.add(ProcessListFragment.newInstance("accept"));
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    private class GetTask extends AsyncTask<Void, Integer, Result<Pair<List<Record>, List<Record>>>> {

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
        protected Result<Pair<List<Record>, List<Record>>> doInBackground(Void... params) {
            try {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/activity/process_list")
                        .addHeader("Authorization","Token " + loggedInUser.token)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject res = new JSONObject(response.body().string());
                    Pair<List<Record>, List<Record>> records = parseRecords(res);
                    return new Result.Success<>(records);
                }
                return new Result.Error(new Exception("请求失败，请联系网站管理员"));
            } catch (Exception e) {
                return new Result.Error(new Exception("网络请求失败，请稍后重试", e));
            }
        }

        private Pair<List<Record>, List<Record>> parseRecords(JSONObject res) throws JSONException, ParseException {
            List<Record> recordsPublish = new ArrayList<>();
            JSONArray resPublish = res.getJSONArray("publish");
            for(int i = 0; i < resPublish.length(); i++){
                JSONObject obj = resPublish.getJSONObject(i);
                Record record = new Record(obj);
                recordsPublish.add(record);
            }

            List<Record> recordsAccept = new ArrayList<>();
            JSONArray resAccept = res.getJSONArray("accept");
            for(int i = 0; i < resAccept.length(); i++){
                JSONObject obj = resAccept.getJSONObject(i);
                Record record = new Record(obj);
                recordsAccept.add(record);
            }

            return new Pair<>(recordsPublish, recordsAccept);
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度（UI线程）
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件（UI线程）
        @Override
        protected void onPostExecute(Result<Pair<List<Record>, List<Record>>> result) {
            if (result instanceof Result.Success) {
                Pair<List<Record>, List<Record>> data = ((Result.Success<Pair<List<Record>, List<Record>>>) result).getData();
                mViewModel.getDataPublish().setValue(data.first);
//                cardAdapterPublish.notifyDataSetChanged();
                mViewModel.getDataAccept().setValue(data.second);
//                cardAdapterAccept.notifyDataSetChanged();
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