package com.example.dacnapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dacnapp.Adapter.CartAdapter;
import com.example.dacnapp.Adapter.OrderDetailAdapter;
import com.example.dacnapp.Model.Order;
import com.example.dacnapp.Model.Product;
import com.example.dacnapp.R;

import java.text.DecimalFormat;

public class OrderDetailActivity extends AppCompatActivity {

    DecimalFormat decimalFormat = new DecimalFormat("#,##0");
    ImageButton btnBack;
    RecyclerView rcvCart;
    TextView txtTotalPrice, txtPayMent, txtSatus;
    Order order;
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
        txtSatus.setText(order.getStatus());
        String gia = decimalFormat.format(Integer.parseInt(order.getTotalPrice()));
        txtTotalPrice.setText(gia);
        txtTotalPrice.append(" ₫");
        txtPayMent.setText(order.getPayment());

        rcvCart.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this));
        adapter = new OrderDetailAdapter(this, order.getListCart());
        rcvCart.setAdapter(adapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(OrderDetailActivity.this, DividerItemDecoration.VERTICAL);
        rcvCart.addItemDecoration(itemDecoration);
    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
    }
}