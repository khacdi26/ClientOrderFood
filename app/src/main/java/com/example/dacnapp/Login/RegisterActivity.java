package com.example.dacnapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dacnapp.MainActivity;
import com.example.dacnapp.R;
import com.example.dacnapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword, edtPasswordXN;
    private Button btnDangKy;
    private ProgressDialog progressDialog;
    private TextView btnBack;
    private FirebaseAuth mAuth;
    private ImageView imageDefaults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUi();

        onListener();
    }

    private void onListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDangKy();
            }
        });
    }

    private void initUi() {
        edtEmail = findViewById(R.id.edtEmailDK);
        edtPassword = findViewById(R.id.edtPasswordDK);
        edtPasswordXN = findViewById(R.id.edtPasswordXN);
        btnDangKy = findViewById(R.id.btnTaoTaiKhoan);
        progressDialog = new ProgressDialog(this);
        btnBack = findViewById(R.id.btnBackDK);
        mAuth = FirebaseAuth.getInstance();
        imageDefaults = findViewById(R.id.imageDefault);
    }

    private boolean checkPass() {
        String password = edtPassword.getText().toString().trim();
        String checkPass = edtPasswordXN.getText().toString().trim();

        return password.equals(checkPass);
    }

    private void onClickDangKy() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String displayName = null;
        String photoUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTpRGUcBVltEkFutN21fIqebRvrgP7fOv4CjcNwuka3BtXR_-jhpd7GheJ_RkvMtSsnsA8&usqp=CAU";
        String phoneNumber = null;
        String roomNumber = null;
        String gender = null;

        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
        } else if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
        } else if (edtPasswordXN.getText().toString().isEmpty()) {
            edtPasswordXN.setError("Vui lòng xác nhận mật khẩu");
            edtPasswordXN.requestFocus();
        } else if (!checkPass()) {
            edtPasswordXN.setError("Mật khẩu xác nhận không đúng");
            edtPasswordXN.requestFocus();
        } else {
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                User user = new User(email, password, displayName, photoUrl, phoneNumber, roomNumber, gender);
                                FirebaseFirestore.getInstance().collection("Users")
                                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegisterActivity.this, "Tạo tài khoản thành công.",
                                                            Toast.LENGTH_SHORT).show();
                                                    // Sign in success, update UI with the signed-in user's information
                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finishAffinity();
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Tạo tài khoản thất bại.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

    }
}