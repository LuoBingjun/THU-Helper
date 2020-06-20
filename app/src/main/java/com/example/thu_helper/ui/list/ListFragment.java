package com.example.thu_helper.ui.list;

import androidx.lifecycle.ViewModelProviders;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListFragment extends Fragment {

    private ListViewModel mViewModel;

    private LoggedInUser loggedInUser;
    private CardAdapter cardAdapter;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        loggedInUser = LoginRepository.getInstance().getUser();
        mViewModel = ViewModelProviders.of(getActivity()).get(ListViewModel.class);

        View root = inflater.inflate(R.layout.fragment_list, container, false);

        // Init RecyclerView
        cardAdapter = new CardAdapter(mViewModel.getData());
        RecyclerView recyclerView = root.findViewById(R.id.recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cardAdapter);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        switch(mViewModel.getType()) {
            case 0:
                new GetWaitingTask().execute();
                break;
            case 1:
                new GetProceedingTask().execute();
                break;
            case 2:
                new GetHistoryTask().execute();
                break;
        }
    }

    private class GetWaitingTask extends AsyncTask<Void, Integer, Result<List<Record>>> {

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
        protected Result<List<Record>> doInBackground(Void... params) {
            try {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/activity/waiting_list")
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
                mViewModel.getData().clear();
                mViewModel.getData().addAll(((Result.Success<List<Record>>) result).getData());
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

    private class GetProceedingTask extends AsyncTask<Void, Integer, Result<List<Record>>> {

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
        protected Result<List<Record>> doInBackground(Void... params) {
            try {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/activity/proceeding_list")
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
                mViewModel.getData().clear();
                mViewModel.getData().addAll(((Result.Success<List<Record>>) result).getData());
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

    private class GetHistoryTask extends AsyncTask<Void, Integer, Result<List<Record>>> {

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
        protected Result<List<Record>> doInBackground(Void... params) {
            try {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/activity/history_list")
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
                mViewModel.getData().clear();
                mViewModel.getData().addAll(((Result.Success<List<Record>>) result).getData());
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