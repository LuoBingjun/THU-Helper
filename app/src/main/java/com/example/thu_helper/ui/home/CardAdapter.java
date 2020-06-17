package com.example.thu_helper.ui.home;

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

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<Record> data;
    public CardAdapter(List<Record> data){
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_record,parent,false);
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
        int id;
        ImageView image;
        TextView title;
        TextView content;
        TextView loc;
        TextView time;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.cardView);
            this.image = itemView.findViewById(R.id.image);
            this.title = itemView.findViewById(R.id.title);
            this.content = itemView.findViewById(R.id.info);
            this.loc = itemView.findViewById(R.id.loc);
            this.time = itemView.findViewById(R.id.time);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = data.get(position);
        holder.id = record.id;
        holder.image.setBackgroundResource(R.drawable.ic_order_128dp);
        holder.title.setText(record.title);
        holder.content.setText(record.content);
        holder.loc.setText(record.title);
        holder.time.setText(new java.text.SimpleDateFormat("MM-dd hh:mm").format(record.time));
    }
}
