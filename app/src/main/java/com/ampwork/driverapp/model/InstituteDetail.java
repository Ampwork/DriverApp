package com.ampwork.driverapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class InstituteDetail implements Parcelable {

    private String address;
    private String amount;
    private String customerId;
    private String email;
    private String name;
    private String numBuses;
    private String password;
    private String payDate;
    private String paymentStatus;
    private String phone;
    private String profileUrl;
    private String referenceId;
    private String subscription;
    private String type;
    private String username;
    private String key;


    public InstituteDetail() {
    }

    public InstituteDetail(String address, String amount, String customerId, String email, String name, String numBuses, String password, String payDate, String paymentStatus, String phone, String profileUrl, String referenceId, String subscription, String type, String username, String key) {
        this.address = address;
        this.amount = amount;
        this.customerId = customerId;
        this.email = email;
        this.name = name;
        this.numBuses = numBuses;
        this.password = password;
        this.payDate = payDate;
        this.paymentStatus = paymentStatus;
        this.phone = phone;
        this.profileUrl = profileUrl;
        this.referenceId = referenceId;
        this.subscription = subscription;
        this.type = type;
        this.username = username;
        this.key = key;
    }

    protected InstituteDetail(Parcel in) {
        address = in.readString();
        amount = in.readString();
        customerId = in.readString();
        email = in.readString();
        name = in.readString();
        numBuses = in.readString();
        password = in.readString();
        payDate = in.readString();
        paymentStatus = in.readString();
        phone = in.readString();
        profileUrl = in.readString();
        referenceId = in.readString();
        subscription = in.readString();
        type = in.readString();
        username = in.readString();
        key = in.readString();
    }

    public static final Creator<InstituteDetail> CREATOR = new Creator<InstituteDetail>() {
        @Override
        public InstituteDetail createFromParcel(Parcel in) {
            return new InstituteDetail(in);
        }

        @Override
        public InstituteDetail[] newArray(int size) {
            return new InstituteDetail[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumBuses() {
        return numBuses;
    }

    public void setNumBuses(String numBuses) {
        this.numBuses = numBuses;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(amount);
        dest.writeString(customerId);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(numBuses);
        dest.writeString(password);
        dest.writeString(payDate);
        dest.writeString(paymentStatus);
        dest.writeString(phone);
        dest.writeString(profileUrl);
        dest.writeString(referenceId);
        dest.writeString(subscription);
        dest.writeString(type);
        dest.writeString(username);
        dest.writeString(key);
    }
}
