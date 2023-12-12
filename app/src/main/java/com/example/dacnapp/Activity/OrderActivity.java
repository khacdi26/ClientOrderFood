package com.example.dacnapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.dacnapp.Adapter.CartAdapter;
import com.example.dacnapp.Adapter.OrderAdapter;
import com.example.dacnapp.Model.Order;
import com.example.dacnapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    ImageButton btnBack;
    RecyclerView rcvOrder;
    ArrayList<Order> listOrder = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        intiUI();
        setValues();
        initListener();
    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setValues() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference ordersRef = firestore.collection("Orders");

        assert user != null;
        Query userOrdersQuery = ordersRef
                .whereEqualTo("userId", user.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING);
        userOrdersQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        System.out.println(documentSnapshot.get("orderID"));
                        Order order = documentSnapshot.toObject(Order.class);
                        System.out.println(order.getOrderID());
                        listOrder.add(order);
                        // Xử lý đơn hàng theo ý muốn
                    }
                    rcvOrder.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
                    OrderAdapter adapter = new OrderAdapter(this, listOrder);
                    rcvOrder.setAdapter(adapter);

                }
            } else {
                // Xử lý khi có lỗi xảy ra trong truy vấn
                Exception exception = task.getException();
                if (exception != null) {
                    // Xử lý exception
                }
            }
        });


    }

    private void intiUI() {
        btnBack = findViewById(R.id.btnBackOrder);
        rcvOrder = findViewById(R.id.rcvOrder);
    }
}