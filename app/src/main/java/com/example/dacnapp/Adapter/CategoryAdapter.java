package com.example.dacnapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dacnapp.Model.Category;
import com.example.dacnapp.Model.Product;
import com.example.dacnapp.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    Context context;
    ArrayList<Category> categories;

    public CategoryAdapter(Context context, ArrayList<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryAdapter.CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.title.setText(category.getTitle());
        Glide.with(holder.thumb.getContext())
                .load(category.getThumb())
                .error(R.mipmap.ic_launcher)
                .into(holder.thumb);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView thumb;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            initUi();
        }


        private void initUi() {
            title = itemView.findViewById(R.id.txtTitleCategory);
            thumb = itemView.findViewById(R.id.imgThumbCategory);
        }
    }
}
