package com.wioletamwrobel.thebucketlistapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BucketListItemAdapter extends RecyclerView.Adapter<BucketListItemAdapter.BucketListItemViewHolder> {

    private BucketListItem[] list;

    public BucketListItemAdapter(List<BucketListItem> list) {
        this.list = list.toArray(new BucketListItem[0]);
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    @NonNull
    @Override
    public BucketListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bucket_list, parent, false);
        return new BucketListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BucketListItemViewHolder holder, int position) {
        holder.bind(list[position]);
    }

    static class BucketListItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView itemImage;
        private TextView itemTitle;

        private RatingBar rating;


        public BucketListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.image_view_item);
            itemTitle = itemView.findViewById(R.id.text_view_item_title);
            rating = itemView.findViewById(R.id.rating_bar);
        }

        public void bind(BucketListItem item) {
            itemImage.setImageResource(item.itemImage);
            itemTitle.setText(item.itemTitle);
            rating.setRating(item.itemRating);
        }
    }
}
