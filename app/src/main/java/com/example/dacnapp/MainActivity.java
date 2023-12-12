package com.example.dacnapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dacnapp.Activity.OrderActivity;
import com.example.dacnapp.Adapter.CartAdapter;
import com.example.dacnapp.DAO.CartDAO;
import com.example.dacnapp.Login.LoginActivity;
import com.example.dacnapp.Activity.CartActivity;
import com.example.dacnapp.Activity.ChangePasswordActivity;
import com.example.dacnapp.Activity.HomeFragment;
import com.example.dacnapp.Activity.ProfileActivity;
import com.example.dacnapp.Activity.SearchActivity;
import com.example.dacnapp.Model.Cart;
import com.example.dacnapp.Model.Category;
import com.example.dacnapp.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int MY_REQUEST_CODE = 10;

    private static final int Fragment_Home = 0;

    private static MainActivity instance;
    private int mCurrentFragment = Fragment_Home;
    private int slCart = 0;
    private Button btnDangXuat;
    private TextView txtTen, txtEmail, badgeCart;
    private ImageView userImage;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnSearch, btnCart;
    MenuItem navChangePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        replaceFragment(new HomeFragment());
        initListener();
        showUserInfor();
        setBadgeCart();
    }

    public void setBadgeCart() {
        CartDAO dao = new CartDAO(MainActivity.this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        ArrayList<Cart> listCart = dao.getAllCart(user.getUid());
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

    private void initListener() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        btnDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    private void initUi() {
        navigationView = findViewById(R.id.nav_view);
        txtEmail = navigationView.getHeaderView(0).findViewById(R.id.userEmail);
        txtTen = navigationView.getHeaderView(0).findViewById(R.id.userName);
        userImage = navigationView.getHeaderView(0).findViewById(R.id.userImage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_draw_open, R.string.nav_draw_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        btnSearch = findViewById(R.id.btnSearch);
        btnCart = findViewById(R.id.btnCart);
        btnDangXuat = findViewById(R.id.btnDangXuat);
        instance = this;
        badgeCart = findViewById(R.id.txtCartBadgeMain);

        Menu menu = navigationView.getMenu();
        navChangePassword = menu.findItem(R.id.nav_changePassword);

    }

    public void showUserInfor() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                // Hiển thị thông tin người dùng
                assert user != null;
                txtEmail.setText(user.getEmail());
                txtTen.setText(user.getDisplayName());
                // Tải hình ảnh người dùng sử dụng Glide
                if (user.getPhotoUrl() != null) {
                    Uri imageUri = Uri.parse(user.getPhotoUrl());
                    Glide.with(MainActivity.this)
                            .load(imageUri)
                            .error(R.mipmap.ic_launcher)
                            .into(userImage);
                } else {
                    // Nếu không có hình ảnh, bạn có thể hiển thị một hình ảnh mặc định
                    Glide.with(MainActivity.this)
                            .load(R.mipmap.ic_launcher)
                            .into(userImage);
                }
                if(user.getPassword()==null){
                    navChangePassword.setVisible(false);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            if (mCurrentFragment != Fragment_Home) {
                replaceFragment(new HomeFragment());
                mCurrentFragment = Fragment_Home;
            }
        } else if (id == R.id.nav_search) {

            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_cart) {

            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_profile) {

            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_changePassword) {

            Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_order) {

            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
            startActivity(intent);

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    public static MainActivity getInstance() {
        return instance;
    }
}