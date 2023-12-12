package com.example.dacnapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dacnapp.Activity.DetailActivity;
import com.example.dacnapp.Activity.OrderDetailActivity;
import com.example.dacnapp.Model.Order;
import com.example.dacnapp.Model.Product;
import com.example.dacnapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    Context context;
    ArrayList<Order> listOrder;
    DecimalFormat decimalFormat = new DecimalFormat("#,##0");

    public OrderAdapter(Context context, ArrayList<Order> listOrder) {
        this.context = context;
        this.listOrder = listOrder;
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderAdapter.OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {
        Order order = listOrder.get(position);

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Products").document(order.getListCart().get(0).getProductId());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Product product = documentSnapshot.toObject(Product.class);

                    assert product != null;
                    Glide.with(holder.imgThumb.getContext())
                            .load(product.getThumb())
                            .error(R.mipmap.ic_launcher)
                            .into(holder.imgThumb);
                    holder.txtTitle.setText(product.getTitle());
                    String gia = decimalFormat.format(Integer.parseInt(product.getPrice()));
                    holder.txtGia.setText(gia);
                    holder.txtGia.append(" ₫");
                }
            }
        });

        holder.txtSoLg.setText("x");
        holder.txtSoLg.append(String.valueOf(order.getListCart().get(0).getSoLg()));

        if (order.getListCart().size() > 1) {
            holder.txtXemThem.setVisibility(View.VISIBLE);
            holder.txtTongSoLg.setText(String.valueOf(order.getListCart().size()));
            holder.txtTongSoLg.append(" sản phẩm");
        } else {
            holder.txtXemThem.setVisibility(View.GONE);
            holder.txtTongSoLg.setVisibility(View.INVISIBLE);
        }

        String tongGia = decimalFormat.format(Integer.parseInt(order.getTotalPrice()));
        holder.txtTongGia.setText(tongGia);
        holder.txtTongGia.append(" ₫");
        holder.txtPayment.setText(order.getPayment());
        holder.txtStatus.setText(order.getStatus());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                /*truyền product sang Detail*/
                intent.putExtra("order", order);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView txtTitle, txtSoLg, txtGia, txtTongSoLg, txtTongGia, txtPayment, txtStatus, txtXemThem;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumbOrder);
            txtTitle = itemView.findViewById(R.id.txtTitleOrder);
            txtSoLg = itemView.findViewById(R.id.txtSoLgOrder);
            txtGia = itemView.findViewById(R.id.txtPriceOrder);
            txtTongSoLg = itemView.findViewById(R.id.txtSoLgTongOrder);
            txtTongGia = itemView.findViewById(R.id.txtTotalPriceOrder);
            txtPayment = itemView.findViewById(R.id.txtPaymentOrder);
            txtStatus = itemView.findViewById(R.id.txtStatusOrder);
            txtXemThem = itemView.findViewById(R.id.txtXemThemOrder);
        }
    }
}
