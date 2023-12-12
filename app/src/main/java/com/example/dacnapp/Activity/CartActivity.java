package com.example.dacnapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dacnapp.DAO.CartDAO;
import com.example.dacnapp.Adapter.CartAdapter;
import com.example.dacnapp.Model.Cart;
import com.example.dacnapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    DecimalFormat decimalFormat = new DecimalFormat("#,##0");
    private RecyclerView rcvCart;
    private Button btnDatMon;
    private TextView txtTotalPrice;
    private CartDAO dao;
    private ArrayList<Cart> listCart;
    private CartAdapter adapter;
    private ImageButton btnBack;
    private FirebaseUser user;
    private String totalPricePay;

    @Override
    protected void onResume() {
        super.onResume();
        setValues();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initUI();
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


        btnDatMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDatMon();
            }
        });
    }

    private void setValues() {
        rcvCart.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        user = FirebaseAuth.getInstance().getCurrentUser();
        dao = new CartDAO(CartActivity.this);
        listCart = dao.getAllCart(user.getUid());

        adapter = new CartAdapter(this, listCart);
        rcvCart.setAdapter(adapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(CartActivity.this, DividerItemDecoration.VERTICAL);
        rcvCart.addItemDecoration(itemDecoration);

        adapter.calculateTotalPrice(new CartAdapter.TotalPriceCallback() {
            @Override
            public void onTotalPriceCalculated(int totalPrice) {
                String gia = decimalFormat.format(totalPrice);
                txtTotalPrice.setText(gia);
                totalPricePay = String.valueOf(totalPrice);
            }
        });
        adapter.setOnCartChangeListener(new CartAdapter.OnCartChangeListener() {
            @Override
            public void onCartChanged() {
                // Cập nhật lại tổng giá trị ở đây
                adapter.calculateTotalPrice(new CartAdapter.TotalPriceCallback() {
                    @Override
                    public void onTotalPriceCalculated(int totalPrice) {
                        String gia = decimalFormat.format(totalPrice);
                        txtTotalPrice.setText(gia);
                        totalPricePay = String.valueOf(totalPrice);
                    }
                });
            }
        });
    }

    private void initUI() {
        btnDatMon = findViewById(R.id.btnOrderCart);
        btnBack = findViewById(R.id.btnBackCart);
        rcvCart = findViewById(R.id.rcvCart);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
    }

    private void onClickDatMon() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userRef = db.collection("Users").document(user.getUid());
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String roomNumber = documentSnapshot.getString("roomNumber");

                        if (roomNumber == null || roomNumber.equals("null")) {
                            // Trường "roomnumber" bằng null, xử lý tương ứng
                            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                            builder.setMessage("Bạn đang không thuê phòng nên không thể đặt món!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Xử lý khi người dùng nhấn nút "OK"
                                            dialog.dismiss();
                                        }
                                    });

                            // Tạo và hiển thị AlertDialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else if (adapter.getItemCount() == 0) {
                            // Chuyển activity tại đây
                            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                            builder.setMessage("Vui lòng thêm sản phẩm vào giỏ trước khi thanh toán!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Xử lý khi người dùng nhấn nút "OK"
                                            dialog.dismiss();
                                        }
                                    });

                            // Tạo và hiển thị AlertDialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                            intent.putExtra("totalPrice", totalPricePay);
                            intent.putExtra("listCart", listCart); // cardList
                            startActivity(intent);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Xử lý lỗi khi không thể lấy dữ liệu người dùng từ Firestore
                }
            });
        }
    }
}