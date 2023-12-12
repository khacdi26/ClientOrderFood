package com.example.dacnapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dacnapp.Model.Cart;
import com.example.dacnapp.Model.Product;
import com.example.dacnapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    DecimalFormat decimalFormat = new DecimalFormat("#,##0");
    Context context;
    ArrayList<Cart> listCart;

    public OrderDetailAdapter(Context context, ArrayList<Cart> listCart) {
        this.context = context;
        this.listCart = listCart;
    }

    @NonNull
    @Override
    public OrderDetailAdapter.OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_cart_order_detail, parent, false);
        return new OrderDetailAdapter.OrderDetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailAdapter.OrderDetailViewHolder holder, int position) {
        Cart cart = listCart.get(position);
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Products").document(cart.getProductId());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    {
                        Product product = documentSnapshot.toObject(Product.class);
                        assert product != null;
                        holder.txtTitle.setText(product.getTitle());
                        String gia = decimalFormat.format(Integer.parseInt(product.getPrice()));
                        holder.txtPrice.setText(gia);
                        holder.txtPrice.append(" ₫");
                        Glide.with(holder.imgThumb.getContext())
                                .load(product.getThumb())
                                .error(R.mipmap.ic_launcher)
                                .into(holder.imgThumb);
                    }
                }
            }
        });
        holder.txtSoLg.setText(String.valueOf(cart.getSoLg()));
    }

    @Override
    public int getItemCount() {
        return listCart.size();
    }

    public class OrderDetailViewHolder extends RecyclerView.ViewHolder {

        ImageView imgThumb;
        TextView txtTitle, txtPrice, txtSoLg;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            imgThumb = itemView.findViewById(R.id.imgThumbOrderDetail);
            txtSoLg = itemView.findViewById(R.id.txtSoLgOrderDetail);
            txtPrice = itemView.findViewById(R.id.txtPriceOrderDetail);
            txtTitle = itemView.findViewById(R.id.txtTitleOrderDetail);
        }
    }
}
