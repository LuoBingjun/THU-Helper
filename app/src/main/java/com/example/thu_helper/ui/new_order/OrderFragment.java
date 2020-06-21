package com.example.thu_helper.ui.new_order;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.thu_helper.R;
import com.example.thu_helper.data.LoginRepository;
import com.example.thu_helper.data.Result;
import com.example.thu_helper.data.model.LoggedInUser;
import com.example.thu_helper.ui.detail.DetailFragment;
import com.example.thu_helper.utils.Global;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrderFragment extends Fragment {

    private OrderViewModel orderViewModel;
    private QMUIGroupListView mGroupListView;
    private Button sendOrderBtn;
    private LoggedInUser loggedInUser;

    public static OrderFragment newInstance() {
        return new OrderFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        orderViewModel =
                ViewModelProviders.of(this).get(OrderViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_order, container, false);

        mGroupListView = root.findViewById(R.id.groupListView);
        initGroupListView(root);
        loggedInUser = LoginRepository.getInstance().getUser();

        sendOrderBtn = root.findViewById(R.id.sendBtn);

        sendOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder();
            }
        });


        return root;
    }

    private void sendOrder() {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle("发布订单")
                .setMessage("确定要发布新订单吗？")
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
                        new OrderTask().execute(
                                orderViewModel.getTitle(),
                                orderViewModel.getBeginTime(),
                                orderViewModel.getEndTime(),
                                orderViewModel.getLocation(),
                                orderViewModel.getMyDetail(),
                                orderViewModel.getMoney()
                        );
                        dialog.dismiss();
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
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

            try {
                OkHttpClient client = new OkHttpClient();
                FormBody formBody = new FormBody
                        .Builder()
                        .add("title", title)
                        .add("start_time", beginTime)
                        .add("end_time", endTime)
                        .add("location", location)
                        .add("activity_info", myDetail)
                        .add("reward", money)
                        .build();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/activity/publish")
                        .addHeader("Authorization","Token " + loggedInUser.token)
                        .post(formBody)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return new Result.Success<>(true);
                }
                return new Result.Error(new Exception("发布失败，请检查填写信息后重试"));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return new Result.Error(new Exception("网络请求失败，请稍后再试", e));
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
                Toast.makeText(getActivity().getApplicationContext(), "新订单发布成功", Toast.LENGTH_LONG).show();
                getActivity().finish();
            } else {
                Toast.makeText(getContext(), ((Result.Error) result).getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        // 方法5：onCancelled()
        // 作用：将异步任务设置为：取消状态（UI线程）
        @Override
        protected void onCancelled() {

        }
    }


    private void alertDialog(final QMUICommonListItemView itemView){

        final String type = itemView.getText().toString();

        switch (type){
            case OrderInputInfo.Title:
            case OrderInputInfo.Detail:
            case OrderInputInfo.Location:
            case OrderInputInfo.Money:
                showStringDialog(itemView);
                break;

            case OrderInputInfo.BeginTime:
            case OrderInputInfo.EndTime:
                showTimeDialogPick(itemView);
                break;
        }

    }

    private void showStringDialog(final QMUICommonListItemView itemView){
        final CharSequence titleType = itemView.getText();
        final Context context = itemView.getContext();
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(context);
        builder.setTitle(titleType.toString());
        builder.setPlaceholder("在此输入内容...");
        builder.addAction("取消", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });

        switch (titleType.toString()){
            case OrderInputInfo.Title:{
                builder.setDefaultText(orderViewModel.getTitle());
                builder.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        orderViewModel.setTitle(text.toString());
                        itemView.setDetailText(text.toString());
                        dialog.dismiss();
                    }
                });
                break;
            }



            case OrderInputInfo.Location:{
                builder.setDefaultText(orderViewModel.getLocation());
                builder.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        orderViewModel.setLocation(text.toString());
                        itemView.setDetailText(text.toString());
                        dialog.dismiss();
                    }
                });
                break;
            }


            case OrderInputInfo.Money:{
                builder.setDefaultText(orderViewModel.getMoney());
                builder.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        orderViewModel.setMoney(text.toString());
                        itemView.setDetailText(text.toString());
                        dialog.dismiss();
                    }
                });
                break;
            }

            case OrderInputInfo.Detail:{
                builder.setDefaultText(orderViewModel.getMyDetail());
                builder.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                builder.addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        orderViewModel.setMyDetail(text.toString());
                        itemView.setDetailText(text.toString());
                        dialog.dismiss();
                    }
                });
                break;
            }
        }
        builder.show();
    }

    private void showTimeDialogPick(final QMUICommonListItemView itemView) {
        Date nowDate;
        try{
            nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(itemView.getDetailText().toString());
        }
        catch(ParseException e){
            nowDate = new Date();
        }

        nowDate.setSeconds(0);

        final Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(nowDate);
        final Date date = nowDate;

        final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            //选择完时间后会调用该回调函数
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                date.setHours(hourOfDay);
                date.setMinutes(minute);

                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                itemView.setDetailText(time);
                switch (itemView.getText().toString()){
                    case OrderInputInfo.BeginTime:
                        orderViewModel.setBeginTime(time);
                        break;
                    case OrderInputInfo.EndTime:
                        orderViewModel.setEndTime(time);
                        break;
                }
            }
        }, nowCalendar.get(Calendar.HOUR_OF_DAY), nowCalendar.get(Calendar.MINUTE), true);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            //选择完日期后会调用该回调函数
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Date _Date = new Date(year - 1900, monthOfYear, dayOfMonth);
                date.setYear(year - 1900);
                date.setMonth(monthOfYear);
                date.setDate(dayOfMonth);

                timePickerDialog.show();
            }
        }, nowCalendar.get(Calendar.YEAR), nowCalendar.get(Calendar.MONTH), nowCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }


    private void initGroupListView(final View root){

        QMUICommonListItemView itemTitle = mGroupListView.createItemView(OrderInputInfo.Title);
        itemTitle.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemTitle.setDetailText(orderViewModel.getTitle());

        QMUICommonListItemView itemBeginTime = mGroupListView.createItemView(OrderInputInfo.BeginTime);
        itemBeginTime.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemBeginTime.setDetailText(orderViewModel.getBeginTime());

        QMUICommonListItemView itemEndTime = mGroupListView.createItemView(OrderInputInfo.EndTime);
        itemEndTime.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemEndTime.setDetailText(orderViewModel.getEndTime());

        QMUICommonListItemView itemLocation = mGroupListView.createItemView(OrderInputInfo.Location);
        itemLocation.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemLocation.setDetailText(orderViewModel.getLocation());

        QMUICommonListItemView itemMoney = mGroupListView.createItemView(OrderInputInfo.Money);
        itemMoney.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemMoney.setDetailText(orderViewModel.getMoney());

        QMUICommonListItemView itemDetail = mGroupListView.createItemView(OrderInputInfo.Detail);
        itemDetail.setOrientation(QMUICommonListItemView.VERTICAL);
        itemDetail.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemDetail.setDetailText(orderViewModel.getMyDetail());
        itemDetail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemDetail.setMinHeight(QMUIResHelper.getAttrDimen(getContext(), R.attr.qmui_list_item_height));

        int paddingVer = QMUIDisplayHelper.dp2px(getContext(), 12);
        itemDetail.setPadding(itemDetail.getPaddingLeft(), paddingVer,
                itemDetail.getPaddingRight(), paddingVer);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    alertDialog((QMUICommonListItemView) v);
                    Toast.makeText(getActivity(), text + " is Clicked", Toast.LENGTH_SHORT).show();
                }
            }
        };//默认文字在左边   自定义加载框按钮

        QMUIGroupListView.newSection(getContext())
                .setTitle("订单信息")
                .addItemView(itemTitle,onClickListener)
                .addItemView(itemMoney,onClickListener)
                .addItemView(itemLocation, onClickListener)
                .addItemView(itemBeginTime, onClickListener)
                .addItemView(itemEndTime, onClickListener)
                .addItemView(itemDetail, onClickListener)
                .addTo(mGroupListView);
    }
}
