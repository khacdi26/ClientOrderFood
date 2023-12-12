package com.example.dacnapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;


import com.example.dacnapp.R;

public class SearchActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText txtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initUi();
        focusSearch();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void focusSearch() {
        txtSearch.requestFocus();
        new Handler().postDelayed(() -> {
            // Hiển thị bàn phím ảo
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(txtSearch, InputMethodManager.SHOW_IMPLICIT);
        }, 500);
    }

    private void initUi() {
        btnBack = findViewById(R.id.btnBackSearch);
        txtSearch = findViewById(R.id.edtSearch);
    }
}