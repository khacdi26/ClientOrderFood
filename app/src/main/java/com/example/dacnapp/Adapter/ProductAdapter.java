package com.example.dacnapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dacnapp.Activity.DetailActivity;
import com.example.dacnapp.Model.Product;
import com.example.dacnapp.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {

    Context context;
    ArrayList<Product> listProduct;
    ArrayList<Product> listProductOld;
    DecimalFormat decimalFormat = new DecimalFormat("#,##0");

    public ProductAdapter(Context context, ArrayList<Product> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
        this.listProductOld = listProduct;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = listProduct.get(position);
        holder.title.setText(product.getTitle());
        String gia = decimalFormat.format(Integer.parseInt(product.getPrice()));
        holder.price.setText(gia);
        holder.price.append(" ₫");
        Glide.with(holder.thumb.getContext())
                .load(product.getThumb())
                .error(R.mipmap.ic_launcher)
                .into(holder.thumb);
        holder.cardProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                /*truyền product sang Detail*/
                intent.putExtra("product", product);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if(strSearch.isEmpty()){
                    listProduct = listProductOld;
                }else {
                    ArrayList<Product> products = new ArrayList<>();
                    for (Product product:listProductOld
                         ) {
                        if(product.getTitle().toLowerCase().contains(strSearch.toLowerCase())){
                            products.add(product);
                        }
                    }
                    listProduct = products;
                }
                FilterResults filterResults =  new FilterResults();
                filterResults.values = listProduct;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listProduct = (ArrayList<Product>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView title, price;
        ImageView thumb;
        CardView cardProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            initUi();
        }

        private void initUi() {
            title = itemView.findViewById(R.id.txtTitleProduct);
            price = itemView.findViewById(R.id.txtPriceProduct);
            thumb = itemView.findViewById(R.id.imgThumbProduct);
            cardProduct = itemView.findViewById(R.id.cardProduct);
        }
    }
}
