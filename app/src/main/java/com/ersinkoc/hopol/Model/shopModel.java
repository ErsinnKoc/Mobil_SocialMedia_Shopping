package com.ersinkoc.hopol.Model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class shopModel {
    private String userName, imageUrl, uid, comments, description, id, productName, price;
    private int likeCount;
    @ServerTimestamp
    private Date timestamp;

    public shopModel() {}

    public shopModel(String userName, String imageUrl, String uid, String comments, String description, String id, int likeCount, Date timestamp, String productName, String price) {
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.comments = comments;
        this.description = description;
        this.id = id;
        this.likeCount = likeCount;
        this.timestamp = timestamp;
        this.productName = productName;
        this.price = price;
    }

    // Getters and setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
