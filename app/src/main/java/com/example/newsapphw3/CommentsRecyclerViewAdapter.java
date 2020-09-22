package com.example.newsapphw3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CommentItem> comments = new ArrayList<>();

    public CommentsRecyclerViewAdapter(Context context, ArrayList<CommentItem> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_layout, parent, false);
        ViewHolder view_holder = new ViewHolder(view);
        return view_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(comments.get(position).name);
        holder.text.setText(comments.get(position).text);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name      = itemView.findViewById(R.id.name_textview);
            text  = itemView.findViewById(R.id.comment_textview);

        }
    }
}
