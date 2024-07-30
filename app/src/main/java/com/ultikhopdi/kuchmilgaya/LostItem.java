package com.ultikhopdi.kuchmilgaya;

import java.util.function.BiPredicate;

public class LostItem {
    private String id;
    private String itemName;
    private String date;
    private String time;
    private String place;
    private String imageUrl;
    private String desc;
    private String contact;
    private String userId;
    private String userReg;
    private String dateReg;
    private String timeReg;
    private Boolean claimed;

    public LostItem() {
        // Default constructor required for calls to DataSnapshot.getValue(LostItem.class)
    }

    public LostItem(String id, String itemName, String date, String time, String place,String desc,String contact, String imageUrl,
                    String userId,String userReg,String dateReg,String timeReg,Boolean claimed) {
        this.id = id;
        this.itemName = itemName;
        this.date = date;
        this.time = time;
        this.place = place;
        this.desc = desc;
        this.contact = contact;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.userReg = userReg;
        this.dateReg=dateReg;
        this.timeReg=timeReg;
        this.claimed=claimed;
    }

    public String getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }

    public String getDesc() {
        return desc;
    }

    public String getContact() {
        return contact;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserReg() {
        return userReg;
    }

    public String getDateReg() {
        return dateReg;
    }

    public String getTimeReg() {
        return timeReg;
    }

    public Boolean getClaimed() {
        return claimed;
    }
}
