package com.wioletamwrobel.thebucketlistapp;

import androidx.annotation.DrawableRes;

public class BucketListItem {
    int itemImage;
    int itemTitle;
    //int itemDescription;
    //int itemDate;
    float itemRating;


    public BucketListItem(@DrawableRes int itemImage, int itemTitle, float rating) {
        this.itemImage = itemImage;
        this.itemTitle = itemTitle;
        this.itemRating = rating;
    }
}
