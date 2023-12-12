package com.example.dacnapp.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dacnapp.Model.Product;
import com.example.dacnapp.R;

import java.util.List;

public class ListImageProductApapter extends RecyclerView.Adapter<ListImageProductApapter.ListImageProductViewHolder> {
    private Context context;
    private List<String> listImage;

    public ListImageProductApapter(Context context, List<String> listImage) {
        this.context = context;
        this.listImage = listImage;
    }

    @NonNull
    @Override
    public ListImageProductApapter.ListImageProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_list_img, null);
        return new ListImageProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListImageProductApapter.ListImageProductViewHolder holder, int position) {
        String image = listImage.get(position);
        Glide.with(holder.img.getContext())
                .load(image)
                .error(R.mipmap.ic_launcher)
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        if (listImage != null) {
            return listImage.size();
        } else {
            return 0;
        }
    }

    public static class ListImageProductViewHolder extends RecyclerView.ViewHolder {

        ImageView img;

        public ListImageProductViewHolder(@NonNull View itemView) {
            super(itemView);
            initUi();
        }

        private void initUi() {
            img = itemView.findViewById(R.id.imgListProduct);
        }
    }
}
