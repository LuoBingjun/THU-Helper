package com.example.thu_helper.ui.detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.thu_helper.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class DetailFragment extends Fragment {

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

        MutableLiveData<RecordDetail> record = mViewModel.getRecord();
        record.observe(this, new Observer<RecordDetail>() {
            @Override
            public void onChanged(@Nullable RecordDetail record) {
                System.out.print(record);
                initGroupListView(record);
            }
        });
    }

    private void initGroupListView(RecordDetail record) {
        // 订单内容
        QMUICommonListItemView itemTitle = mGroupListView.createItemView(record.title);

        QMUICommonListItemView itemBeginTime = mGroupListView.createItemView("开始时间");
        itemBeginTime.setOrientation(QMUICommonListItemView.HORIZONTAL);
        itemBeginTime.setDetailText(new java.text.SimpleDateFormat("MM-dd hh:mm").format(record.time));

        QMUICommonListItemView itemEndTime = mGroupListView.createItemView("结束时间");
        itemEndTime.setOrientation(QMUICommonListItemView.HORIZONTAL);
        itemEndTime.setDetailText(new java.text.SimpleDateFormat("MM-dd hh:mm").format(record.time));

        QMUICommonListItemView itemLoc = mGroupListView.createItemView("地点");
        itemLoc.setOrientation(QMUICommonListItemView.HORIZONTAL);
        itemLoc.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemLoc.setDetailText(record.loc);

        QMUICommonListItemView itemContent = mGroupListView.createItemView(null,
                "详情",
                record.content,
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int paddingVer = QMUIDisplayHelper.dp2px(getContext(), 12);
        itemContent.setPadding(itemContent.getPaddingLeft(), paddingVer,
                itemContent.getPaddingRight(), paddingVer);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    Toast.makeText(getActivity(), text + " is Clicked", Toast.LENGTH_SHORT).show();
                    if (((QMUICommonListItemView) v).getAccessoryType() == QMUICommonListItemView.ACCESSORY_TYPE_SWITCH) {
                        ((QMUICommonListItemView) v).getSwitch().toggle();
                    }
                }
            }
        };

        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.newSection(getContext())
                .setTitle("订单内容")
//                .setDescription("Section 1 的描述")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(itemTitle, onClickListener)
                .addItemView(itemBeginTime, onClickListener)
                .addItemView(itemEndTime, onClickListener)
                .addItemView(itemLoc, onClickListener)
                .addItemView(itemContent, onClickListener)
                .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0)
                .addTo(mGroupListView);


        // 订单状态

//        QMUICommonListItemView itemLoc = mGroupListView.createItemView("地点");
//        itemLoc.setOrientation(QMUICommonListItemView.VERTICAL);
//        itemLoc.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
//        itemLoc.setDetailText(record.loc);
        switch(record.state){
            case 0:{
                // 待接单
                QMUICommonListItemView itemState = mGroupListView.createItemView("状态");
                itemState.setOrientation(QMUICommonListItemView.HORIZONTAL);
                itemState.setDetailText("等待接单");
                QMUICommonListItemView itemOrder = mGroupListView.createItemView("我要接单");
                itemOrder.setOrientation(QMUICommonListItemView.HORIZONTAL);
                itemOrder.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

                View.OnClickListener onClickListener2 = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v instanceof QMUICommonListItemView) {
                            CharSequence text = ((QMUICommonListItemView) v).getText();
                            Toast.makeText(getActivity(), text + " is Clicked", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                QMUIGroupListView.newSection(getContext())
                        .setTitle("订单操作")
                        .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                        .addItemView(itemState, null)
                        .addItemView(itemOrder, onClickListener2)
                        .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0)
                        .addTo(mGroupListView);
                break;
            }

            case 1:{
                QMUICommonListItemView itemState = mGroupListView.createItemView("状态");
                itemState.setOrientation(QMUICommonListItemView.HORIZONTAL);
                itemState.setDetailText("进行中");
                QMUICommonListItemView itemChat = mGroupListView.createItemView("在线交流");
                itemChat.setOrientation(QMUICommonListItemView.HORIZONTAL);
                itemChat.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
                QMUICommonListItemView itemComplete = mGroupListView.createItemView("完成订单");
                itemComplete.setOrientation(QMUICommonListItemView.HORIZONTAL);
                itemComplete.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
                QMUICommonListItemView itemCancel = mGroupListView.createItemView("取消订单");
                itemCancel.setOrientation(QMUICommonListItemView.HORIZONTAL);
                itemCancel.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

                View.OnClickListener onClickListener2 = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v instanceof QMUICommonListItemView) {
                            CharSequence text = ((QMUICommonListItemView) v).getText();
                            Toast.makeText(getActivity(), text + " is Clicked", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                QMUIGroupListView.newSection(getContext())
                        .setTitle("订单操作")
                        .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                        .addItemView(itemState, null)
                        .addItemView(itemChat, onClickListener2)
                        .addItemView(itemComplete, onClickListener2)
                        .addItemView(itemCancel, onClickListener2)
                        .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0)
                        .addTo(mGroupListView);
                break;
            }

            case 2:{
                QMUICommonListItemView itemState = mGroupListView.createItemView("状态");
                itemState.setOrientation(QMUICommonListItemView.HORIZONTAL);
                itemState.setDetailText("已完成");
                QMUIGroupListView.newSection(getContext())
                        .setTitle("订单操作")
                        .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                        .addItemView(itemState, null)
                        .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0)
                        .addTo(mGroupListView);
                break;
            }
        }

    }

}
