package com.example.dacnapp.Activity;

import static android.app.PendingIntent.getActivity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dacnapp.MainActivity;
import com.example.dacnapp.Model.User;
import com.example.dacnapp.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnBack, btnEdit;
    private Button btnSave;
    private EditText edtEmail, edtFullName, edtPhoneNumber;
    private TextView txtRoomNumber;
    private ImageView userImage;
    private ProgressDialog progressDialog;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final DocumentReference userRef;
    User thisUser;

    {
        assert user != null;
        userRef = db.collection("Users").document(user.getUid());
    }

    MainActivity mainActivity = MainActivity.getInstance();
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initUi();
        progressDialog = new ProgressDialog(this);
        setUserInfor();
        initListener();
        userImage.setClickable(false);

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedUri = data.getData();
                            Glide.with(this).load(selectedUri).error(R.mipmap.ic_launcher).into(userImage);
                        }
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

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEdit();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave();
            }
        });
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImage();
            }
        });
    }

    private void onClickImage() {
        ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                .createIntent(new Function1<Intent, Unit>() {
                    @Override
                    public Unit invoke(Intent intent) {
                        imagePickLauncher.launch(intent);
                        return null;
                    }
                });
    }

    private void setUserInfor() {
        if (user != null) {
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    thisUser = documentSnapshot.toObject(User.class);

                    // Hiển thị thông tin người dùng
                    assert thisUser != null;
                    edtEmail.setText(thisUser.getEmail());
                    edtFullName.setText(thisUser.getDisplayName());
                    edtPhoneNumber.setText(thisUser.getPhoneNumber());
                    txtRoomNumber.setText(thisUser.getRoomNumber());
                    // Tải hình ảnh người dùng sử dụng Glide
                    if (thisUser.getPhotoUrl() != null) {
                        Uri imageUri = Uri.parse(thisUser.getPhotoUrl());
                        Glide.with(ProfileActivity.this)
                                .load(imageUri)
                                .error(R.mipmap.ic_launcher)
                                .into(userImage);
                    } else {
                        // Nếu không có hình ảnh, bạn có thể hiển thị một hình ảnh mặc định
                        Glide.with(ProfileActivity.this)
                                .load(R.mipmap.ic_launcher)
                                .into(userImage);
                    }
                }
            });
        }
    }

    private void onClickSave() {
        if (user == null) {
            return;
        }
        progressDialog.show();
        updateProfile();
        progressDialog.dismiss();
    }

    private void updateProfile() {
        String newEmail = edtEmail.getText().toString().trim();

        if (!newEmail.equals(Objects.requireNonNull(user.getEmail()).toString().trim())) {
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
                                        // Update the user's email address
                                        user.verifyBeforeUpdateEmail(newEmail)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Send an email verification to the new email address
                                                            user.updateEmail(newEmail);
                                                            updateInfor();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });

                }
            });
        } else {
            updateInfor();
        }
    }

    private void updateInfor() {
        String newEmail = edtEmail.getText().toString().trim();
        String newFullName = edtFullName.getText().toString().trim();
        String newPhoneNumber = edtPhoneNumber.getText().toString().trim();
        // Lấy Uri của hình ảnh được chọn

        if (selectedUri != null) {
            uploadImageToFirestorage(selectedUri);
        } else {
            updateUserInfo(null); // Tiếp tục cập nhật thông tin trong collection Firestore ngay cả khi không có hình ảnh mới
        }

        // Thêm các thông tin cần cập nhật khác vào map 'updates'
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", newEmail);
        updates.put("displayName", newFullName);
        updates.put("phoneNumber", newPhoneNumber);

        // Đoạn code cập nhật thông tin trong collection Firestore đã được di chuyển vào phương thức 'updateUserInfo()'
    }

    private void uploadImageToFirestorage(Uri imageUri) {
        // Tạo tên file duy nhất cho hình ảnh
        String fileName = UUID.randomUUID().toString();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // Tham chiếu đến vị trí lưu trữ hình ảnh trong Firestorage
        StorageReference imageRef = storageRef.child("images/" + fileName);

        // Tải hình ảnh lên Firestorage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy URL của hình ảnh đã tải lên
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Tiếp tục cập nhật thông tin trong collection Firestore
                        updateUserInfo(uri.toString());
                    }).addOnFailureListener(e -> {
                        // Xảy ra lỗi khi lấy URL hình ảnh từ Firestorage
                        Toast.makeText(ProfileActivity.this, "Lỗi khi lấy URL hình ảnh.", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    // Xảy ra lỗi khi tải hình ảnh lên Firestorage
                    Toast.makeText(ProfileActivity.this, "Lỗi khi tải hình ảnh lên.", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserInfo(String photoUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("photoUrl", photoUrl);

        // Thêm các thông tin cần cập nhật khác vào map 'updates'

        userRef.update(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "Cập nhật thông tin thành công.", Toast.LENGTH_SHORT).show();
                // Tiếp tục với các hoạt động khác sau khi cập nhật thành công
                recreate();
                mainActivity.showUserInfor();
            } else {
                Toast.makeText(ProfileActivity.this, "Cập nhật thông tin thất bại.", Toast.LENGTH_SHORT).show();
            }
        });
    }
/*    private void updateInfor() {
        String newEmail = edtEmail.getText().toString().trim();
        String newFullName = edtFullName.getText().toString().trim();
        String newPhoneNumber = edtPhoneNumber.getText().toString().trim();
        String newUri = String.valueOf(selectedUri);

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", newEmail);
        updates.put("displayName", newFullName);
        updates.put("phoneNumber", newPhoneNumber);
        if (selectedUri != null) {
            updates.put("photoUrl", newUri);
        }

// Thêm các thông tin cần cập nhật khác vào map 'updates'

        userRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Cập nhật thông tin thành công.", Toast.LENGTH_SHORT).show();
                    // Tiếp tục với các hoạt động khác sau khi cập nhật thành công
                    recreate();
                    mainActivity.showUserInfor();
                } else {
                    Toast.makeText(ProfileActivity.this, "Cập nhật thông tin thất bại.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/


    private void onClickEdit() {
        btnSave.setVisibility(View.VISIBLE);
        edtFullName.setEnabled(true);
        edtPhoneNumber.setEnabled(true);

        edtFullName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtFullName, InputMethodManager.SHOW_IMPLICIT);
        if(thisUser.getPassword()!=null){
            edtEmail.setEnabled(true);
        }
        userImage.setClickable(true);
    }

    private void initUi() {
        btnBack = findViewById(R.id.btnBackProfile);
        btnEdit = findViewById(R.id.btnProEdit);
        btnSave = findViewById(R.id.btnProSave);
        edtEmail = findViewById(R.id.userProEmail);
        edtFullName = findViewById(R.id.userProFullName);
        edtPhoneNumber = findViewById(R.id.userProPhoneNumber);
        userImage = findViewById(R.id.userProImage);
        txtRoomNumber = findViewById(R.id.userProRoom);

    }

}