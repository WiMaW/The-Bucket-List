package com.wioletamwrobel.thebucketlistapp;

import androidx.annotation.DrawableRes;

public class BucketListItem {
    String itemImage;
    String itemTitle;
    //int itemDescription;
    //int itemDate;
    float itemRating;


    public BucketListItem(String itemImage, String itemTitle, float rating) {
        this.itemImage = itemImage;
        this.itemTitle = itemTitle;
        this.itemRating = rating;
    }

    public String getItemImagePath() {return itemImage;}
    public String getItemTitle() {return itemTitle;}
    public float getItemRating() {return itemRating;}
}
