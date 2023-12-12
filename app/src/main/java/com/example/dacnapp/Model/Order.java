package com.example.dacnapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Order implements Parcelable {
    private String orderID;
    private String userId;
    private String roomId;
    private ArrayList<Cart> listCart;
    private String totalPrice;
    private String dateTime;
    private String payment;
    private String status;
    private String token;

    public Order() {

    }

    public Order(String id, String userId, ArrayList<Cart> listCart, String totalPrice, String dateTime, String payment, String status, String token, String roomId) {
        this.orderID = id;
        this.userId = userId;
        this.listCart = listCart;
        this.totalPrice = totalPrice;
        this.dateTime = dateTime;
        this.payment = payment;
        this.status = status;
        this.token = token;
        this.roomId = roomId;
    }

    public String getRoomId() {


        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Cart> getListCart() {
        return listCart;
    }

    public void setListCart(ArrayList<Cart> listCart) {
        this.listCart = listCart;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String stauts) {
        this.status = stauts;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Parcelable implementation
    protected Order(Parcel in) {
        orderID = in.readString();
        userId = in.readString();
        roomId = in.readString();
        listCart = in.readArrayList(Cart.class.getClassLoader(), Cart.class);
        totalPrice = in.readString();
        dateTime = in.readString();
        payment = in.readString();
        status = in.readString();
        token = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderID);
        dest.writeString(userId);
        dest.writeString(roomId);
        dest.writeList(listCart);
        dest.writeString(totalPrice);
        dest.writeString(dateTime);
        dest.writeString(payment);
        dest.writeString(status);
        dest.writeString(token);
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];

        }
    };
}
