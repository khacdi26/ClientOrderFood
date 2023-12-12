package com.example.dacnapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dacnapp.Login.LoginActivity;
import com.example.dacnapp.Model.User;
import com.example.dacnapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {
    public ImageButton btnBack;
    public TextView txtEmail;
    public EditText edtNewPass;
    public ImageView imageUser;
    public Button btnChange;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final DocumentReference userRef;

    {
        assert user != null;
        userRef = db.collection("Users").document(user.getUid());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initUI();
        setUserInfor();
        initListener();

    }

    private void setUserInfor() {
        if (user != null) {
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User thisUser = documentSnapshot.toObject(User.class);

                    // Hiển thị thông tin người dùng
                    assert thisUser != null;
                    txtEmail.setText(thisUser.getEmail());
                    // Tải hình ảnh người dùng sử dụng Glide
                    if (thisUser.getPhotoUrl() != null) {
                        Uri imageUri = Uri.parse(thisUser.getPhotoUrl());
                        Glide.with(ChangePasswordActivity.this)
                                .load(imageUri)
                                .error(R.mipmap.ic_launcher)
                                .into(imageUser);
                    } else {
                        // Nếu không có hình ảnh, bạn có thể hiển thị một hình ảnh mặc định
                        Glide.with(ChangePasswordActivity.this)
                                .load(R.mipmap.ic_launcher)
                                .into(imageUser);
                    }
                }
            });
        }
    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String newPass = edtNewPass.getText().toString().trim();
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User thisUser = documentSnapshot.toObject(User.class);

                // Reauthenticate the user with their current credentials
                assert thisUser != null;
                AuthCredential credential = EmailAuthProvider.getCredential(thisUser.getEmail(), thisUser.getPassword());
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(newPass);
                                    updateInfor();
                                }
                            }
                        });
            }
        });

    }

    private void updateInfor() {
        String newPassword = edtNewPass.getText().toString().trim();

        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());

        Map<String, Object> updates = new HashMap<>();
        updates.put("password", newPassword);
// Thêm các thông tin cần cập nhật khác vào map 'updates'

        userRef.update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, "Cập nhật thông tin thành công.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Cập nhật thông tin thất bại.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initUI() {
        btnBack = findViewById(R.id.btnBackChangePass);
        txtEmail = findViewById(R.id.edtEmailChangePass);
        edtNewPass = findViewById(R.id.edtNewPass);
        imageUser = findViewById(R.id.imageUserChangePass);
        btnChange = findViewById(R.id.btnChangePass);

        edtNewPass.requestFocus();
        new Handler().postDelayed(() -> {
            // Hiển thị bàn phím ảo
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtNewPass, InputMethodManager.SHOW_IMPLICIT);
        }, 500);
    }
}