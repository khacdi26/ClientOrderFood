package com.example.dacnapp.Helper;

import android.text.InputFilter;
import android.text.Spanned;

public class PositiveNumberInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String input = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());
        if (input.isEmpty()) {
            return null;  // Cho phép xóa văn bản
        }

        int value = Integer.parseInt(input);
        if (value >= 0) {
            return null;  // Cho phép giá trị số dương
        }

        return "";  // Không cho phép giá trị âm
    }
}
