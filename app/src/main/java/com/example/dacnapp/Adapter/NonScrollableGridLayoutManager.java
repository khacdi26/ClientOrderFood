package com.example.dacnapp.Adapter;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;

public class NonScrollableGridLayoutManager extends GridLayoutManager {
    public NonScrollableGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public boolean canScrollVertically() {
        return false; // Lúc này, không cho phép cuộn theo chiều dọc
    }
}