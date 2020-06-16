package com.example.thu_helper.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thu_helper.R;

import java.util.List;

public class ListViewAdapter extends BaseAdapter {
    private List<Record> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public ListViewAdapter(Context context, List<Record> data){
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }
    /**
     * 组件集合，对应list.xml中的控件
     * @author Administrator
     */
    public final class Component{
        public ImageView image;
        public TextView title;
        public TextView content;
        public TextView loc;
        public TextView time;

        Component(View view){
            this.image = (ImageView)view.findViewById(R.id.image);
            this.title = (TextView)view.findViewById(R.id.title);
            this.content = (TextView)view.findViewById(R.id.info);
            this.loc = (TextView)view.findViewById(R.id.loc);
            this.time = (TextView)view.findViewById(R.id.time);
        }
    }
    @Override
    public int getCount() {
        return data.size();
    }
    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Component component = null;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.listview_home, null);
            component = new Component(convertView);
            convertView.setTag(component);
        }else{
            component=(Component)convertView.getTag();
        }

        //绑定数据
        Record record = data.get(position);

        component.image.setBackgroundResource(R.drawable.ic_order_128dp);
        component.title.setText(record.title);
        component.content.setText(record.content);
        component.loc.setText(record.title);
        component.time.setText(new java.text.SimpleDateFormat("MM-dd hh:mm").format(record.time));
        return convertView;
    }
}
