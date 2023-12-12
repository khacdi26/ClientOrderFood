package com.example.dacnapp.Model;

public class User {
    private String email;
    private String password;
    private String displayName;
    private String photoUrl;
    private String phoneNumber;
    private String roomNumber;
    private String gender;

    public User() {

    }

    public User(String email, String password, String displayName, String photoUrl, String phoneNumber, String roomNumber, String gender) {
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
        this.roomNumber = roomNumber;
        this.gender = gender;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRoomNumber() {
        return this.roomNumber;
    }


    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;

    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
