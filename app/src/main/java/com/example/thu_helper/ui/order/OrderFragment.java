package com.example.thu_helper.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.thu_helper.R;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class OrderFragment extends Fragment {

    private OrderViewModel orderViewModel;
    QMUIGroupListView mGroupListView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        orderViewModel =
                ViewModelProviders.of(this).get(OrderViewModel.class);
        View root = inflater.inflate(R.layout.fragment_order, container, false);
        mGroupListView = root.findViewById(R.id.groupListView);

        initGroupListView();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    Toast.makeText(getActivity(), text + " is Clicked", Toast.LENGTH_SHORT).show();
                }
            }
        };//默认文字在左边   自定义加载框按钮

        
        /*
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        return root;
    }

    private void initGroupListView(){

        QMUICommonListItemView itemTitle = mGroupListView.createItemView("标题");

        QMUICommonListItemView itemBeginTime = mGroupListView.createItemView("开始时间");
                itemBeginTime.setDetailText("06-17-14-23");

        QMUICommonListItemView itemEndTime = mGroupListView.createItemView("结束时间");
        itemEndTime.setDetailText("06-17-14-24");

        QMUICommonListItemView itemLocation = mGroupListView.createItemView("地点");
        itemLocation.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemLocation.setDetailText("紫荆14号楼后小树林");

        QMUICommonListItemView itemDetail = mGroupListView.createItemView("详情");
        itemDetail.setOrientation(QMUICommonListItemView.VERTICAL);
        itemDetail.setDetailText("最近不在学校帮忙取快递");

        QMUICommonListItemView itemMoney = mGroupListView.createItemView("赏金");
        itemMoney.setDetailText("5元");


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    Toast.makeText(getActivity(), text + " is Clicked", Toast.LENGTH_SHORT).show();
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
