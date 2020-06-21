package com.example.thu_helper.ui.process;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thu_helper.R;
import com.example.thu_helper.ui.detail.DetailActivity;
import com.example.thu_helper.ui.home.Record;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<Record> data;
    public CardAdapter(List<Record> data){
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_record_process, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Record record = data.get(position);
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("id", record.id);
                v.getContext().startActivity(intent);
            }
        });
        return viewHolder;
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;
        TextView content;
        TextView loc;
        TextView time;
        TextView reward;
        TextView state;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.cardView);
//            this.image = itemView.findViewById(R.id.image);
            this.title = itemView.findViewById(R.id.title);
            this.content = itemView.findViewById(R.id.info);
            this.loc = itemView.findViewById(R.id.loc);
            this.time = itemView.findViewById(R.id.time);
            this.reward = itemView.findViewById(R.id.reward);
            this.state = itemView.findViewById(R.id.state);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = data.get(position);
        holder.title.setText(record.title);
        holder.content.setText(record.content);
        holder.loc.setText(record.loc);
        holder.reward.setText(record.reward);
        holder.time.setText(record.formatTime());
        switch(record.state){
            case 0:
                holder.state.setText("待接单");
                break;
            case 1:
                holder.state.setText("进行中");
                break;
            case 2:
                holder.state.setText("已完成");
                break;
        }

    }
}
