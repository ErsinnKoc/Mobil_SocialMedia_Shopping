package com.ersinkoc.hopol.Model;

import android.net.Uri;

public class GalleryImageModel {
    public Uri picUri;
    public GalleryImageModel() {
    }

    public GalleryImageModel(Uri picUri) {
        this.picUri = picUri;
    }


    public Uri getPicUri() {
        return picUri;
    }

    public void setPicUri(Uri picUri) {
        this.picUri = picUri;
    }


}
