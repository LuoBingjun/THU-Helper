package com.example.thu_helper.ui.order;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.example.thu_helper.R;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
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
        initGroupListView(root);

        return root;
    }

    private void alertDialog(final QMUICommonListItemView itemView){
        CharSequence title = itemView.getText();
        final Context context = itemView.getContext();
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(context);
        builder.setTitle(title.toString());

        //builder.setInputType(InputType.TYPE_CLASS_DATETIME);

        switch (itemView.getText().toString()){
            case OrderInputType.Title:
            case OrderInputType.Detail:
            case OrderInputType.Location:
            case OrderInputType.Money:
                builder.setPlaceholder("在此输入内容...");
                builder.setInputType(InputType.TYPE_CLASS_TEXT);
                break;

            case OrderInputType.BeginTime:
            case OrderInputType.EndTime:
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
                itemView.setDetailText(text);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void initGroupListView(final View root){

        LiveData<OrderInputType> orderInputType = orderViewModel.getOrder();
        QMUICommonListItemView itemTitle = mGroupListView.createItemView(OrderInputType.Title);
        itemTitle.setDetailText(orderInputType.getValue().title);

        QMUICommonListItemView itemBeginTime = mGroupListView.createItemView(OrderInputType.BeginTime);
        itemBeginTime.setDetailText(orderInputType.getValue().beginTime);

        QMUICommonListItemView itemEndTime = mGroupListView.createItemView(OrderInputType.EndTime);
        itemEndTime.setDetailText(orderInputType.getValue().endTime);

        QMUICommonListItemView itemLocation = mGroupListView.createItemView(OrderInputType.Location);
        itemLocation.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemLocation.setDetailText(orderInputType.getValue().location);

        QMUICommonListItemView itemDetail = mGroupListView.createItemView(OrderInputType.Detail);
        itemDetail.setOrientation(QMUICommonListItemView.VERTICAL);
        itemDetail.setDetailText(orderInputType.getValue().detail);

        QMUICommonListItemView itemMoney = mGroupListView.createItemView(OrderInputType.Money);
        itemMoney.setDetailText(orderInputType.getValue().money);


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
