package com.example.dacnapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dacnapp.Adapter.CartAdapter;
import com.example.dacnapp.Adapter.OrderDetailAdapter;
import com.example.dacnapp.Model.Order;
import com.example.dacnapp.Model.Product;
import com.example.dacnapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

public class OrderDetailActivity extends AppCompatActivity {

    DecimalFormat decimalFormat = new DecimalFormat("#,##0");
    ImageButton btnBack;
    RecyclerView rcvCart;
    TextView txtTotalPrice, txtPayMent, txtSatus;
    Order order;
    Button btnXacNhan;
    private OrderDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initUI();
        setValues();
        initListener();
    }

    private void setValues() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Orders").document(order.getOrderID());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Order mOrder = documentSnapshot.toObject(Order.class);

                txtSatus.setText(mOrder.getStatus());
                String gia = decimalFormat.format(Integer.parseInt(mOrder.getTotalPrice()));
                txtTotalPrice.setText(gia);
                txtTotalPrice.append(" ₫");
                txtPayMent.setText(mOrder.getPayment());

                rcvCart.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this));
                adapter = new OrderDetailAdapter(OrderDetailActivity.this, mOrder.getListCart());
                rcvCart.setAdapter(adapter);

                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(OrderDetailActivity.this, DividerItemDecoration.VERTICAL);
                rcvCart.addItemDecoration(itemDecoration);
            }
        });
    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickXacNhan();
            }
        });
    }

    private void onClickXacNhan() {
        String status = "Đã nhận được hàng";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Orders").document(order.getOrderID());
        docRef.update("status",status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setValues();
                btnXacNhan.setVisibility(View.GONE);
            }
        });

    }

    private void initUI() {
        Intent intent = getIntent();
        order = (Order) intent.getParcelableExtra("order", Order.class);

        rcvCart = findViewById(R.id.rcvCartOrDe);
        txtSatus = findViewById(R.id.txtStatusOrderDetail);
        txtPayMent = findViewById(R.id.txtPaymentOrderDetail);
        txtTotalPrice = findViewById(R.id.txtTotalPriceOrderDetail);
        btnBack = findViewById(R.id.btnBackOrderDetail);
        btnXacNhan = findViewById(R.id.btnXacNhanDeOr);
    }
}