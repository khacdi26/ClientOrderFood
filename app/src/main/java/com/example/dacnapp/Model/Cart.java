package com.example.dacnapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Cart implements Parcelable {


    private String productId;
    private int soLg;

    public Cart() {

    }

    public Cart(String id, String productTitle, String productThumb, String productPrice, int soLg) {
        this.productId = id;

        this.soLg = soLg;
    }

    public String getProductId() {
        return this.productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }


    public int getSoLg() {
        return this.soLg;
    }

    public void setSoLg(int soLg) {
        this.soLg = soLg;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.productId);
        dest.writeInt(this.soLg);
    }

    protected Cart(Parcel in) {
        // Đọc dữ liệu từ Parcel và gán giá trị cho thuộc tính của lớp Cart
        this.productId = in.readString();
        this.soLg = in.readInt();
    }

    // Triển khai Creator cho giao diện Parcelable
    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };
}
