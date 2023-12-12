package com.example.dacnapp.Activity;

import static com.google.firebase.firestore.FieldValue.serverTimestamp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dacnapp.DAO.CartDAO;
import com.example.dacnapp.Login.LoginActivity;
import com.example.dacnapp.MainActivity;
import com.example.dacnapp.Model.Order;
import com.example.dacnapp.Model.User;
import com.example.dacnapp.Model.Cart;
import com.example.dacnapp.R;
import com.example.dacnapp.Zalopay.Api.CreateOrderZalo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.OrderRequest;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButtonContainer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentActivity extends AppCompatActivity {

    private PaymentButtonContainer paymentButtonContainer;
    private ImageButton btnBack;
    private Button btnCashPay, btnZaloPay;
    private TextView txtTotal, txtRoom;
    private EditText edtName, edtPhone;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private String totalPrice, orderId;
    private ArrayList<Cart> cartList;
    private MainActivity mainActivity = MainActivity.getInstance();
    DecimalFormat decimalFormat = new DecimalFormat("#,##0");


    private static final double EXCHANGE_RATE = 0.000041; // Tỷ giá VNĐ sang USD

    public static double vndToUsd(double amountVnd) {
        double amountUsd = amountVnd * EXCHANGE_RATE;
        BigDecimal bd = new BigDecimal(amountUsd);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        //ZaloPay
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        initUI();
        setValues();
        initListener();
    }

    private void initListener() {
        paymentButtonContainer.setup(new CreateOrder() {
                                         @Override
                                         public void create(@NonNull CreateOrderActions createOrderActions) {

                                             double amount = Double.parseDouble(totalPrice);
                                             if (edtName.getText().toString().isEmpty()) {
                                                 edtName.setError("Vui lòng nhập đủ họ tên");
                                                 edtName.requestFocus();
                                             } else if (edtPhone.getText().toString().isEmpty()) {
                                                 edtPhone.setError("Vui lòng nhập số điện thoại");
                                                 edtPhone.requestFocus();
                                             } else {
                                                 updateUser();
                                                 ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                                                 purchaseUnits.add(
                                                         new PurchaseUnit.Builder()
                                                                 .amount(
                                                                         new Amount.Builder()
                                                                                 .currencyCode(CurrencyCode.USD)
                                                                                 .value(String.valueOf(vndToUsd(amount)))
                                                                                 .build()
                                                                 )
                                                                 .build()
                                                 );
                                                 OrderRequest order = new OrderRequest(
                                                         OrderIntent.CAPTURE,
                                                         new AppContext.Builder()
                                                                 .userAction(UserAction.PAY_NOW)
                                                                 .build(),
                                                         purchaseUnits
                                                 );
                                                 createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);

                                             }
                                         }
                                     },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                String payment = "Thanh toán thành công qua PayPal";
                                Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                                Toast.makeText(PaymentActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                                createOrder(payment, new CreateOrderCallback() {
                                    @Override
                                    public void onCreateOrderSuccess(String orderId) {
                                        deleteCart();
                                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finishAffinity();
                                    }

                                    @Override
                                    public void onCreateOrderFailure() {
                                        Toast.makeText(mainActivity, "Lỗi khi tạo order", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
        );
        btnZaloPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickZaloPay();
            }
        });
        btnCashPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCashPay();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void onClickCashPay() {
        String payment = "Thanh toán tiền mặt";
        if (edtName.getText().toString().isEmpty()) {
            edtName.setError("Vui lòng nhập đủ họ tên");
            edtName.requestFocus();
        } else if (edtPhone.getText().toString().isEmpty()) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            edtPhone.requestFocus();
        } else {
            updateUser();
            createOrder(payment, new CreateOrderCallback() {
                @Override
                public void onCreateOrderSuccess(String orderId) {
                    deleteCart();
                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }

                @Override
                public void onCreateOrderFailure() {

                }
            });
        }
    }

    private void onClickZaloPay() {
        String payment = "Thanh toán qua Zalo Pay";
        if (edtName.getText().toString().isEmpty()) {
            edtName.setError("Vui lòng nhập đủ họ tên");
            edtName.requestFocus();
        } else if (edtPhone.getText().toString().isEmpty()) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            edtPhone.requestFocus();
        } else {
            updateUser();
            createOrder(payment, new CreateOrderCallback() {
                @Override
                public void onCreateOrderSuccess(String orderId) {
                    zaloPay(orderId);
                    deleteCart();
                }

                @Override
                public void onCreateOrderFailure() {
                    Toast.makeText(mainActivity, "Lỗi khi tạo order", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public interface CreateOrderCallback {
        void onCreateOrderSuccess(String orderId);

        void onCreateOrderFailure();
    }

    private void deleteCart() {
        CartDAO dao = new CartDAO(PaymentActivity.this);
        for (Cart item : cartList) {
            dao.deleteCartItem(user.getUid(), item.getProductId());
        }
        mainActivity.setBadgeCart();
    }

    private void createOrder(String payment, CreateOrderCallback callback) {
        // Lấy ngày giờ hiện tại
        LocalDateTime now = LocalDateTime.now();
        // Định dạng giờ, phút, buổi, ngày, tháng và năm
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a dd/MM/yyyy");

        String formattedDateTime = now.format(formatter);
        String userId = user.getUid();
        String total = totalPrice;
        String status = "Đang chờ xác nhận đơn hàng";
        userRef = db.collection("Users").document(user.getUid());
        if (user != null) {
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User thisUser = documentSnapshot.toObject(User.class);

                    // Hiển thị thông tin người dùng
                    assert thisUser != null;

                    String roomId = thisUser.getRoomNumber();
                    CollectionReference ordersCollection = FirebaseFirestore.getInstance().collection("Orders");
// Tạo một đối tượng Order
                    Order order = new Order();
                    order.setUserId(userId);
                    order.setListCart(cartList);
                    order.setTotalPrice(total);
                    order.setDateTime(formattedDateTime);
                    order.setPayment(payment);
                    order.setStatus(status);
                    order.setToken(null);
                    order.setRoomId(roomId);

// Lưu đối tượng Order vào Firestore
                    ordersCollection.add(order)
                            .addOnSuccessListener(documentReference -> {
                                // Thành công
                                orderId = documentReference.getId();
                                // Cập nhật orderId của đối tượng Order
                                DocumentReference orderRef = ordersCollection.document(documentReference.getId());
                                orderRef.update("orderID", orderId);
                                orderRef.update("timestamp", serverTimestamp());
                                System.out.println(orderId + "dâda");

                                callback.onCreateOrderSuccess(orderId);

                            })
                            .addOnFailureListener(e -> {
                                // Xảy ra lỗi
                                Toast.makeText(mainActivity, "Lỗi khi tạo order", Toast.LENGTH_SHORT).show();
                                callback.onCreateOrderFailure();
                            });
                }
            });
        }
    }

    private void cashPay() {
    }

    private void updateUser() {
        String newFullName = edtName.getText().toString().trim();
        String newPhoneNumber = edtPhone.getText().toString().trim();

        Map<String, Object> updates = new HashMap<>();
        updates.put("displayName", newFullName);
        updates.put("phoneNumber", newPhoneNumber);

        userRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mainActivity.showUserInfor();
                } else {
                    Toast.makeText(PaymentActivity.this, "Cập nhật thông tin thất bại.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void zaloPay(String orderID) {
        CreateOrderZalo orderApi = new CreateOrderZalo();

        try {
            JSONObject data = orderApi.createOrder(txtTotal.getText().toString().trim());
            String code = data.getString("return_code");

            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");

                CollectionReference ordersCollection = FirebaseFirestore.getInstance().collection("Orders");
                ordersCollection.document(orderID).update("token", token);

                ZaloPaySDK.getInstance().payOrder(PaymentActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {

                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {

                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {

                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    private void setValues() {
        String gia = decimalFormat.format(Integer.parseInt(totalPrice));
        txtTotal.setText(gia);
        txtTotal.append(" ₫");
        userRef = db.collection("Users").document(user.getUid());
        if (user != null) {
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User thisUser = documentSnapshot.toObject(User.class);

                    // Hiển thị thông tin người dùng
                    assert thisUser != null;
                    edtName.setText(thisUser.getDisplayName());
                    edtPhone.setText(thisUser.getPhoneNumber());
                    txtRoom.setText(thisUser.getRoomNumber());

                }
            });
        }
    }

    private void initUI() {
        totalPrice = getIntent().getStringExtra("totalPrice");
        cartList = getIntent().getParcelableArrayListExtra("listCart", Cart.class);

        btnBack = findViewById(R.id.btnBackPayment);
        btnCashPay = findViewById(R.id.btnCashPay);
        btnZaloPay = findViewById(R.id.btnZaloPay);
        txtTotal = findViewById(R.id.txtTotalPricePmt);
        txtRoom = findViewById(R.id.txtRoomNumPmt);
        edtName = findViewById(R.id.edtFullNamePmt);
        edtPhone = findViewById(R.id.edtPhoneNumPmt);
        paymentButtonContainer = findViewById(R.id.payment_button_container);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }
}