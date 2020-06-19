package com.example.thu_helper.ui.order;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.thu_helper.R;
import com.example.thu_helper.data.Result;
import com.example.thu_helper.utils.Global;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrderFragment extends Fragment {

    private OrderViewModel orderViewModel;
    private View mFakeStatusBar;
    private QMUIGroupListView mGroupListView;
    private Button sendOrderBtn;
    private Button cancelOrderBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        orderViewModel =
                ViewModelProviders.of(this).get(OrderViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_order, container, false);

        // Set height of fake_status_bar
        mFakeStatusBar = root.findViewById(R.id.fake_status_bar);
        Resources resources = getContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int statusHeight = resources.getDimensionPixelSize(resourceId);
        ConstraintLayout.LayoutParams params=new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, statusHeight);
        mFakeStatusBar.setLayoutParams(params);

        mGroupListView = root.findViewById(R.id.groupListView);
        initGroupListView(root);

        sendOrderBtn = root.findViewById(R.id.sendBtn);
        cancelOrderBtn = root.findViewById(R.id.cancelBtn);

        sendOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OrderTask().execute(
                        orderViewModel.getTitle(),
                        orderViewModel.getBeginTime(),
                        orderViewModel.getEndTime(),
                        orderViewModel.getLocation(),
                        orderViewModel.getMyDetail(),
                        orderViewModel.getMoney()
                );
            }
        });
        return root;
    }

    private class OrderTask extends AsyncTask<String, Integer, Result<Boolean>> {


        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作（UI线程）
        @Override
        protected void onPreExecute() {

        }

        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果（子线程）
        // 此处通过计算从而模拟“加载进度”的情况
        @Override
        protected Result<Boolean> doInBackground(String... strings) {
            String title = strings[0];
            String beginTime = strings[1];
            String endTime = strings[2];
            String location = strings[3];
            String myDetail = strings[4];
            String money = strings[5];
            //publisher_id缺失
            try {
                OkHttpClient client = new OkHttpClient();
                FormBody formBody = new FormBody
                        .Builder()
                        .add("title", title)
                        .add("address",location)
                        .add("start_time", beginTime)
                        .add("end_time",endTime)
                        .add("activity_info", myDetail)
                        .add("reward", money)
                        .add("publisher_id","11111111")
                        .build();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/activity/edit_activity_info")
                        .post(formBody)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return new Result.Success<>(true);
                }
                return new Result.Error(new IOException("Publish Order Error"));
            } catch (Exception e) {
                return new Result.Error(new IOException("Error Publish", e));
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
                Boolean data = ((Result.Success<Boolean>) result).getData();
                Toast.makeText(getContext(), "发布成功！", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), ((Result.Error) result).toString(), Toast.LENGTH_SHORT).show();
            }
        }

        // 方法5：onCancelled()
        // 作用：将异步任务设置为：取消状态（UI线程）
        @Override
        protected void onCancelled() {

        }
    }


    private void alertDialog(final QMUICommonListItemView itemView){
        CharSequence title = itemView.getText();
        final Context context = itemView.getContext();
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(context);
        builder.setTitle(title.toString());

        //builder.setInputType(InputType.TYPE_CLASS_DATETIME);

        final String type = itemView.getText().toString();

        switch (type){
            case OrderInputInfo.Title:
            case OrderInputInfo.Detail:
            case OrderInputInfo.Location:
            case OrderInputInfo.Money:
                builder.setPlaceholder("在此输入内容...");
                builder.setInputType(InputType.TYPE_CLASS_TEXT);
                break;

            case OrderInputInfo.BeginTime:
            case OrderInputInfo.EndTime:
                builder.setPlaceholder("06-17 14:24");
                builder.setInputType(InputType.TYPE_CLASS_DATETIME);
                break;
        }

        builder.addAction("取消", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });

        builder.addAction("确定", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                CharSequence text = builder.getEditText().getText();
                if(text.length() == 0) return;
                itemView.setDetailText(text);
                switch (type){
                    case OrderInputInfo.Title:
                        orderViewModel.setTitle(text.toString());
                        break;

                    case OrderInputInfo.BeginTime:
                        orderViewModel.setBeginTime(text.toString());
                        break;

                    case OrderInputInfo.EndTime:
                        orderViewModel.setEndTime(text.toString());
                        break;

                    case OrderInputInfo.Location:
                        orderViewModel.setLocation(text.toString());
                        break;

                    case OrderInputInfo.Money:
                        orderViewModel.setMoney(text.toString());
                        break;

                    case OrderInputInfo.Detail:
                        orderViewModel.setMyDetail(text.toString());
                        break;
                }

                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void initGroupListView(final View root){

        QMUICommonListItemView itemTitle = mGroupListView.createItemView(OrderInputInfo.Title);
        itemTitle.setDetailText(orderViewModel.getTitle());

        QMUICommonListItemView itemBeginTime = mGroupListView.createItemView(OrderInputInfo.BeginTime);
        itemBeginTime.setDetailText(orderViewModel.getBeginTime());

        QMUICommonListItemView itemEndTime = mGroupListView.createItemView(OrderInputInfo.EndTime);
        itemEndTime.setDetailText(orderViewModel.getEndTime());

        QMUICommonListItemView itemLocation = mGroupListView.createItemView(OrderInputInfo.Location);
        itemLocation.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemLocation.setDetailText(orderViewModel.getLocation());

        QMUICommonListItemView itemDetail = mGroupListView.createItemView(OrderInputInfo.Detail);
        itemDetail.setOrientation(QMUICommonListItemView.VERTICAL);
        itemDetail.setDetailText(orderViewModel.getMyDetail());

        QMUICommonListItemView itemMoney = mGroupListView.createItemView(OrderInputInfo.Money);
        itemMoney.setDetailText(orderViewModel.getMoney());


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    alertDialog((QMUICommonListItemView) v);
                    switch (text.toString()){
                        case OrderInputInfo.Title:
                        case OrderInputInfo.BeginTime:
                        case OrderInputInfo.EndTime:
                        case OrderInputInfo.Location:
                        case OrderInputInfo.Detail:
                        case OrderInputInfo.Money:
                            Toast.makeText(getActivity(), text + " is Clicked", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };//默认文字在左边   自定义加载框按钮

        QMUIGroupListView.newSection(getContext())
                .setTitle("订单")
                .setDescription("自定义字段")
                .addItemView(itemTitle,onClickListener)
                .addItemView(itemBeginTime, onClickListener)
                .addItemView(itemEndTime, onClickListener)
                .addItemView(itemLocation, onClickListener)
                .addItemView(itemDetail, onClickListener)
                .addItemView(itemMoney,onClickListener)
                .addTo(mGroupListView);

    }
}
