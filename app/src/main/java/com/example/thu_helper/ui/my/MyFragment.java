package com.example.thu_helper.ui.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thu_helper.BR;
import com.example.thu_helper.R;
import com.example.thu_helper.data.LoginRepository;
import com.example.thu_helper.data.model.LoggedInUser;
import com.example.thu_helper.ui.AboutActivity;
import com.example.thu_helper.ui.list.ListActivity;
import com.example.thu_helper.ui.setting.SettingActivity;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class MyFragment extends Fragment {
    ViewDataBinding binding;
    private MyViewModel myViewModel;
    private QMUIGroupListView mGroupListView;
    private LoggedInUser loggedInUser;
    private ImageView mImageView;

    @Override
    public void onResume() {
        super.onResume();
        if (loggedInUser.avater != null){
            Glide.with(getContext()).load(loggedInUser.avater)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mImageView);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myViewModel =
                ViewModelProviders.of(this).get(MyViewModel.class);
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_my,container,false);
        loggedInUser = LoginRepository.getInstance().getUser();
        binding.setVariable(BR.loggedInUser, loggedInUser);

        View root = binding.getRoot();
        mImageView = root.findViewById(R.id.h_head);

        mGroupListView = root.findViewById(R.id.groupListView);
        initGroupListView();
        return root;
    }

    private void initGroupListView() {
        QMUICommonListItemView itemPersonInformation = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_gerenxinxi),
                "个人信息",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        View.OnClickListener onClickListener0 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        };

        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.newSection(getContext())
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(itemPersonInformation, onClickListener0)
                .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0)
                .addTo(mGroupListView);


        QMUICommonListItemView itemWaitingRecord = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_waiting),
                "等待中订单",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemWaitingRecord.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        View.OnClickListener onClickListener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        };

        QMUICommonListItemView itemProceddingRecord = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_dingdan),
                "进行中订单",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemProceddingRecord.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        View.OnClickListener onClickListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        };

        QMUICommonListItemView itemHistoryRecord = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_lishi),
                "历史订单",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemHistoryRecord.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        View.OnClickListener onClickListener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
            }
        };

        QMUIGroupListView.newSection(getContext())
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(itemWaitingRecord, onClickListener1)
                .addItemView(itemProceddingRecord, onClickListener2)
                .addItemView(itemHistoryRecord, onClickListener3)
                .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0)
                .addTo(mGroupListView);

        QMUICommonListItemView itemAbout = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_guanyuwomen),
                "关于我们",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemAbout.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        View.OnClickListener onClickListener4 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        };

        QMUIGroupListView.newSection(getContext())
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(itemAbout, onClickListener4)
                .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0)
                .addTo(mGroupListView);
    }
}
