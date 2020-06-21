package com.example.thu_helper.ui.detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.thu_helper.R;
import com.example.thu_helper.data.LoginRepository;
import com.example.thu_helper.data.Result;
import com.example.thu_helper.data.model.LoggedInUser;
import com.example.thu_helper.ui.chatting.ChatActivity;
import com.example.thu_helper.ui.home.Record;
import com.example.thu_helper.utils.Global;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailFragment extends Fragment {
    LoggedInUser loggedInUser;
    DetailViewModel mViewModel;
    QMUIGroupListView mGroupListView;

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        mViewModel = ViewModelProviders.of(getActivity()).get(DetailViewModel.class);
        mGroupListView = root.findViewById(R.id.groupListView);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loggedInUser = LoginRepository.getInstance().getUser();
        MutableLiveData<RecordDetail> record = mViewModel.getRecord();
        record.observe(this, new Observer<RecordDetail>() {
            @Override
            public void onChanged(@Nullable RecordDetail record) {
                initGroupListView(record);
            }
        });
        new GetTask().execute();
    }

    private void initGroupListView(final RecordDetail record) {
        mGroupListView.removeAllViews();

        // 订单内容
        QMUICommonListItemView itemTitle = mGroupListView.createItemView(record.title);

        QMUICommonListItemView itemPublisher = mGroupListView.createItemView("发布者");
        itemPublisher.setOrientation(QMUICommonListItemView.HORIZONTAL);
        itemPublisher.setDetailText(record.publisher_nickname);

        QMUICommonListItemView itemReward = mGroupListView.createItemView("报酬");
        itemReward.setOrientation(QMUICommonListItemView.HORIZONTAL);
        itemReward.setDetailText(record.reward);

        QMUICommonListItemView itemLoc = mGroupListView.createItemView("地点");
        itemLoc.setOrientation(QMUICommonListItemView.HORIZONTAL);
        itemLoc.setDetailText(record.loc);

        QMUICommonListItemView itemBeginTime = mGroupListView.createItemView("开始时间");
        itemBeginTime.setOrientation(QMUICommonListItemView.HORIZONTAL);
        itemBeginTime.setDetailText(new java.text.SimpleDateFormat("MM-dd hh:mm").format(record.start_time));

        QMUICommonListItemView itemEndTime = mGroupListView.createItemView("结束时间");
        itemEndTime.setOrientation(QMUICommonListItemView.HORIZONTAL);
        itemEndTime.setDetailText(new java.text.SimpleDateFormat("MM-dd hh:mm").format(record.end_time));



        QMUICommonListItemView itemContent = mGroupListView.createItemView(null,
                "详情",
                record.content,
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int paddingVer = QMUIDisplayHelper.dp2px(getContext(), 12);
        itemContent.setPadding(itemContent.getPaddingLeft(), paddingVer,
                itemContent.getPaddingRight(), paddingVer);

//        View.OnClickListener onClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v instanceof QMUICommonListItemView) {
//                    CharSequence text = ((QMUICommonListItemView) v).getText();
//                    Toast.makeText(getActivity(), text + " is Clicked", Toast.LENGTH_SHORT).show();
//                    if (((QMUICommonListItemView) v).getAccessoryType() == QMUICommonListItemView.ACCESSORY_TYPE_SWITCH) {
//                        ((QMUICommonListItemView) v).getSwitch().toggle();
//                    }
//                }
//            }
//        };

        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.newSection(getContext())
                .setTitle("订单内容")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(itemTitle, null)
                .addItemView(itemPublisher, null)
                .addItemView(itemReward, null)
                .addItemView(itemLoc, null)
                .addItemView(itemBeginTime, null)
                .addItemView(itemEndTime, null)
                .addItemView(itemContent, null)
                .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0)
                .addTo(mGroupListView);


        // 订单状态
        QMUIGroupListView.Section newSection = QMUIGroupListView.newSection(getContext())
                .setTitle("订单操作")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);

        switch(record.state){
            case 0:{
                // 待接单
                QMUICommonListItemView itemState = mGroupListView.createItemView("状态");
                itemState.setOrientation(QMUICommonListItemView.HORIZONTAL);
                itemState.setDetailText("待接单");

                newSection.addItemView(itemState, null);

                if (loggedInUser.username.equals(record.publisher_id)){
                    QMUICommonListItemView itemCancel = mGroupListView.createItemView("删除订单");
                    itemCancel.setOrientation(QMUICommonListItemView.HORIZONTAL);
                    itemCancel.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
                    View.OnClickListener onClickCancel = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelRecord();
                        }
                    };
                    newSection.addItemView(itemCancel, onClickCancel);
                }
                else{
                    QMUICommonListItemView itemOrder = mGroupListView.createItemView("我要接单");
                    itemOrder.setOrientation(QMUICommonListItemView.HORIZONTAL);
                    itemOrder.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

                    View.OnClickListener onClickAccept = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acceptRecord();
                        }
                    };

                    newSection.addItemView(itemOrder, onClickAccept);
                }
                break;
            }

            case 1:{
                QMUICommonListItemView itemState = mGroupListView.createItemView("状态");
                itemState.setOrientation(QMUICommonListItemView.HORIZONTAL);
                itemState.setDetailText("进行中");
                newSection.addItemView(itemState, null);

                QMUICommonListItemView itemIdentity = mGroupListView.createItemView("角色");
                itemIdentity.setOrientation(QMUICommonListItemView.HORIZONTAL);
                if (loggedInUser.username.equals(record.publisher_id)){
                    itemIdentity.setDetailText("发单方");
                    newSection.addItemView(itemIdentity, null);
                }
                else if (loggedInUser.username.equals(record.acceptor_id)){
                    itemIdentity.setDetailText("接单方");
                    newSection.addItemView(itemIdentity, null);
                }

                if(!loggedInUser.username.equals(record.publisher_id)){
                    QMUICommonListItemView itemPublisherName = mGroupListView.createItemView("联系发单方");
                    itemPublisherName.setOrientation(QMUICommonListItemView.HORIZONTAL);
                    itemPublisherName.setDetailText(record.publisher_nickname);
                    itemPublisherName.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
                    View.OnClickListener onClickPublisher = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("other_id", record.publisher_id);
                            getActivity().startActivity(intent);
                        }
                    };
                    newSection.addItemView(itemPublisherName, onClickPublisher);
                }

                if(!loggedInUser.username.equals(record.acceptor_id)){
                    QMUICommonListItemView itemAcceptorName = mGroupListView.createItemView("联系接单方");
                    itemAcceptorName.setOrientation(QMUICommonListItemView.HORIZONTAL);
                    itemAcceptorName.setDetailText(record.acceptor_nickname);
                    itemAcceptorName.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
                    View.OnClickListener onClickAcceptor = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("other_id", record.acceptor_id);
                            getActivity().startActivity(intent);
                        }
                    };
                    newSection.addItemView(itemAcceptorName, onClickAcceptor);
                }


                if (loggedInUser.username.equals(record.publisher_id)){
                    QMUICommonListItemView itemFinish = mGroupListView.createItemView("完成订单");
                    itemFinish.setOrientation(QMUICommonListItemView.HORIZONTAL);
                    itemFinish.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
                    View.OnClickListener onClickFinish = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishRecord();
                        }
                    };
                    newSection.addItemView(itemFinish, onClickFinish);
                }
                break;
            }

            case 2:{
                QMUICommonListItemView itemState = mGroupListView.createItemView("状态");
                itemState.setOrientation(QMUICommonListItemView.HORIZONTAL);
                itemState.setDetailText("已完成");
                newSection.addItemView(itemState, null);

                QMUICommonListItemView itemPublisherName = mGroupListView.createItemView("发单人");
                itemPublisherName.setOrientation(QMUICommonListItemView.HORIZONTAL);
                itemPublisherName.setDetailText(record.publisher_nickname);
                newSection.addItemView(itemPublisherName, null);

                QMUICommonListItemView itemAcceptorName = mGroupListView.createItemView("接单人");
                itemAcceptorName.setOrientation(QMUICommonListItemView.HORIZONTAL);
                itemAcceptorName.setDetailText(record.acceptor_nickname);
                newSection.addItemView(itemAcceptorName, null);
                break;
            }

        }

        newSection.setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0)
                .addTo(mGroupListView);
    }

    private void cancelRecord(){
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle("删除订单")
                .setMessage("确定要删除吗？")
                .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        new CancelTask().execute();
                        dialog.dismiss();
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    private void acceptRecord() {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle("我要接单")
                .setMessage("确定要接单吗？")
                .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        new AcceptTask().execute();
                        dialog.dismiss();
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    private void finishRecord() {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle("确认完成")
                .setMessage("确定要完成订单吗？")
                .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        new FinishTask().execute();
                        dialog.dismiss();
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    private class GetTask extends AsyncTask<Void, Integer, Result<RecordDetail>> {

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
        protected Result<RecordDetail> doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/activity/info?ID=" + mViewModel.getId().getValue())
                        .addHeader("Authorization","Token " + loggedInUser.token)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject res = new JSONObject(response.body().string());
                    RecordDetail record = new RecordDetail(res);
                    return new Result.Success<>(record);
                }
                return new Result.Error(new Exception("请求失败，请联系网站管理员"));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return new Result.Error(new Exception("网络请求失败，请稍后重试", e));
            }
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度（UI线程）
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件（UI线程）
        @Override
        protected void onPostExecute(Result<RecordDetail> result) {
            if (result instanceof Result.Success) {
                mViewModel.getRecord().setValue(((Result.Success<RecordDetail>) result).getData());
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

    private class CancelTask extends AsyncTask<Void, Integer, Result<Boolean>> {

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
        protected Result<Boolean> doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/activity/del?ID=" + mViewModel.getId().getValue())
                        .addHeader("Authorization","Token " + loggedInUser.token)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return new Result.Success<>(true);
                }
                return new Result.Error(new Exception("请求失败，请联系网站管理员"));
            } catch (Exception e) {
                return new Result.Error(new Exception("网络请求失败，请稍后重试", e));
            }
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度（UI线程）
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件（UI线程）
        @Override
        protected void onPostExecute(Result<Boolean> result) {
            if (result instanceof Result.Success) {
                Toast.makeText(getActivity().getApplicationContext(), "取消成功", Toast.LENGTH_LONG).show();
                getActivity().finish();
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

    private class AcceptTask extends AsyncTask<Void, Integer, Result<Boolean>> {

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
        protected Result<Boolean> doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/activity/accept?ID=" + mViewModel.getId().getValue() + "&accept")
                        .addHeader("Authorization","Token " + loggedInUser.token)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return new Result.Success<>(true);
                }
                return new Result.Error(new Exception("请求失败，请联系网站管理员"));
            } catch (Exception e) {
                return new Result.Error(new Exception("网络请求失败，请稍后重试", e));
            }
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度（UI线程）
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件（UI线程）
        @Override
        protected void onPostExecute(Result<Boolean> result) {
            if (result instanceof Result.Success) {
                Toast.makeText(getActivity().getApplicationContext(), "接单成功，咕咕咕", Toast.LENGTH_LONG).show();
                getActivity().finish();
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

    private class FinishTask extends AsyncTask<Void, Integer, Result<Boolean>> {

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
        protected Result<Boolean> doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/activity/finish?ID=" + mViewModel.getId().getValue())
                        .addHeader("Authorization","Token " + loggedInUser.token)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return new Result.Success<>(true);
                }
                return new Result.Error(new Exception("请求失败，请联系网站管理员"));
            } catch (Exception e) {
                return new Result.Error(new Exception("网络请求失败，请稍后重试", e));
            }
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度（UI线程）
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件（UI线程）
        @Override
        protected void onPostExecute(Result<Boolean> result) {
            if (result instanceof Result.Success) {
                Toast.makeText(getActivity().getApplicationContext(), "订单已完成，记得给ta说声谢谢哦", Toast.LENGTH_LONG).show();
                getActivity().finish();
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
