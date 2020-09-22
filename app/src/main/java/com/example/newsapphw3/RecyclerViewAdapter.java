package com.example.newsapphw3;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<NewsItem> news = new ArrayList<>();

    public RecyclerViewAdapter(Context context, ArrayList<NewsItem> news) {
        this.context = context;
        this.news = news;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_layout, parent, false);
        ViewHolder view_holder = new ViewHolder(view);
        return view_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Glide.with(context).asBitmap().load(news.get(position).image_url).into(holder.imageView);
        holder.date_textView.setText(news.get(position).date);
        holder.title_textView.setText(news.get(position).title);
        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsItem item = news.get(position);
                Intent intent = new Intent(context, NewsDetailsActivity.class);
                intent.putExtra("news_id", item.id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
        }
        });
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView  date_textView;
        TextView  title_textView;
        RelativeLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView      = itemView.findViewById(R.id.imageView);
            date_textView  = itemView.findViewById(R.id.textView2);
            title_textView = itemView.findViewById(R.id.textView3);
            parent_layout  = itemView.findViewById(R.id.parent_layout);
        }
    }
}
