package com.example.mycontactlist;

import android.graphics.Bitmap;

import java.util.Calendar;

public class Contact {

    private int contactId;
    private String contactName;
    private Bitmap picture;

    public void setPicture(Bitmap b) {
        picture = b;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int i) {
        contactId = i;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String s) {
        contactName = s;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String s) {
        streetAddress = s;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String s) {
        city = s;
    }

    public String getState() {
        return state;
    }

    public void setState(String s) {
        state = s;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String s) {
        zipCode = s;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String s) {
        phoneNumber = s;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String s) {
        cellNumber = s;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String s) {
        eMail = s;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar c) {
        birthday = c;
    }

    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String phoneNumber;
    private String cellNumber;
    private String eMail;
    private Calendar birthday;

    public Contact() {
        contactId = -1;
        birthday = Calendar.getInstance();
    }
}
