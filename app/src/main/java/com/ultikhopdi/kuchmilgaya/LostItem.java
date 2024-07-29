package com.ultikhopdi.kuchmilgaya;
public class LostItem {
    private String id;
    private String itemName;
    private String date;
    private String time;
    private String place;
    private String imageUrl;
    private String desc;
    private String contact;

    public LostItem() {
        // Default constructor required for calls to DataSnapshot.getValue(LostItem.class)
    }

    public LostItem(String id, String itemName, String date, String time, String place,String desc,String contact, String imageUrl) {
        this.id = id;
        this.itemName = itemName;
        this.date = date;
        this.time = time;
        this.place = place;
        this.desc = desc;
        this.contact = contact;
        this.imageUrl = imageUrl;
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
}
