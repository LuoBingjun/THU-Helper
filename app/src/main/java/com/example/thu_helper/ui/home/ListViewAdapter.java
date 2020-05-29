package com.example.thu_helper.ui.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.thu_helper.R;

import java.util.List;
import java.util.Map;

public class ListViewAdapter extends BaseAdapter {
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public ListViewAdapter(Context context, List<Map<String, Object>> data){
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
        public TextView info;
        public TextView loc;
        public TextView time;

        Component(View view){
            this.image=(ImageView)view.findViewById(R.id.image);
            this.title=(TextView)view.findViewById(R.id.title);
            this.info=(TextView)view.findViewById(R.id.info);
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
        component.image.setBackgroundResource((Integer)data.get(position).get("image"));
        component.title.setText((String)data.get(position).get("title"));
        component.info.setText((String)data.get(position).get("info"));
        component.loc.setText((String)data.get(position).get("loc"));
//        Drawable drawable = ContextCompat.getDrawable(context,R.drawable.ic_clock_32dp);
//        drawable.setBounds(0,0,drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        component.loc.setCompoundDrawables(drawable,null,null,null);
//        component.loc.setCompoundDrawablePadding(10);
        component.time.setText((String)data.get(position).get("time"));
        return convertView;
    }
}
