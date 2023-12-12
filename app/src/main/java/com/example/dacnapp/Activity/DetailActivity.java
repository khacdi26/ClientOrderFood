package com.example.dacnapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dacnapp.DAO.CartDAO;
import com.example.dacnapp.Adapter.ListImageProductApapter;
import com.example.dacnapp.MainActivity;
import com.example.dacnapp.Model.Cart;
import com.example.dacnapp.Model.Product;
import com.example.dacnapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    private TextView title, price, description, badgeCart;
    private ImageButton btnBack, btnCart;
    private RecyclerView rcvListImg;
    private Button btnAddCart;
    ListImageProductApapter listImgAdapter;
    List<String> listImg;
    private Product product;
    MainActivity mainActivity = MainActivity.getInstance();
    private int slCart = 0;
    DecimalFormat decimalFormat = new DecimalFormat("#,##0");
    private static DetailActivity instance;

    public static DetailActivity getInstance() {
        return instance;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBadgeCart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        product = (Product) intent.getParcelableExtra("product", Product.class);
        initUI();
        setBadgeCart();
        setColum();
        initListener();
    }

    @SuppressLint("SetTextI18n")
    private void setColum() {

        listImg = product.getListImage();
        listImg.add(0, product.getThumb());
        listImgAdapter = new ListImageProductApapter(this, listImg);
        rcvListImg.setAdapter(listImgAdapter);

        title.setText(product.getTitle());
        price.setText("Giá: ");
        String gia = decimalFormat.format(Integer.parseInt(product.getPrice()));
        price.append(gia);
        price.append(" ₫");
        description.setText(product.getDescription());
    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addToCart() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        new CartDAO(this).addToCart(product, userId);
        Toast.makeText(this, "Thêm giỏ hàng thành công", Toast.LENGTH_SHORT).show();
        mainActivity.setBadgeCart();
        setBadgeCart();
    }

    private void initUI() {
        btnBack = findViewById(R.id.btnBackDetail);
        rcvListImg = findViewById(R.id.rcvListImage);
        rcvListImg.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rcvListImg);

        title = findViewById(R.id.text_title);
        price = findViewById(R.id.text_price);
        description = findViewById(R.id.text_description);
        btnAddCart = findViewById(R.id.btnAddToCart);
        btnCart = findViewById(R.id.btnCartDetail);
        badgeCart = findViewById(R.id.txtCartBadgeDetail);
    }

    public void setBadgeCart() {
        CartDAO dao = new CartDAO(DetailActivity.this);
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        ArrayList<Cart> listCart = dao.getAllCart(userId);
        int sl = 0;
        for (Cart cartItem : listCart) {
            sl += cartItem.getSoLg();
        }
        slCart = sl;
        if (slCart == 0) {
            badgeCart.setVisibility(View.GONE);
        } else {
            badgeCart.setVisibility(View.VISIBLE);
            badgeCart.setText(String.valueOf(slCart));
        }
    }
}