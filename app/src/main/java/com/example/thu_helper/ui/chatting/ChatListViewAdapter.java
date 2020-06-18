package com.example.thu_helper.ui.chatting;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thu_helper.R;

import java.util.List;

public class ChatListViewAdapter extends ArrayAdapter<ChatMsgEntity> {

    private int resourceId;
    private LayoutInflater mInflater;
    public ChatListViewAdapter(Context context,int textViewresourceId, List<ChatMsgEntity> datas){
        super(context, textViewresourceId, datas);
        resourceId = textViewresourceId;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChatMsgEntity msg = getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = mInflater.inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = view.findViewById(R.id.right_layout);
            viewHolder.msg_left = view.findViewById(R.id.chatContent_left);
            viewHolder.msg_right = view.findViewById(R.id.chatContent_right);
            viewHolder.sendTime_left = view.findViewById(R.id.sendTime_left);
            viewHolder.sendTime_right = view.findViewById(R.id.sendTime_right);
            viewHolder.username_left = view.findViewById(R.id.chatUsername_left);
            viewHolder.username_right = view.findViewById(R.id.chatUsername_right);
            view.setTag(viewHolder);
        }
        else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        switch (msg.getType()){
            case ChatMsgEntity.MSG_RECEIVED:
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.username_left.setText(msg.getName());
                viewHolder.sendTime_left.setText(msg.getDate());
                viewHolder.msg_left.setText(msg.getText());
                break;
            case ChatMsgEntity.MSG_SEND:
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.username_right.setText(msg.getName());
                viewHolder.sendTime_right.setText(msg.getDate());
                viewHolder.msg_right.setText(msg.getText());
                break;
        }

        return view;
    }

    static class ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView sendTime_left;
        TextView sendTime_right;
        TextView username_left;
        TextView username_right;
        TextView msg_left;
        TextView msg_right;
    }
}
