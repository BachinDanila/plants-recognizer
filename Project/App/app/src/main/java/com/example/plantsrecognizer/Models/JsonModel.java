package com.example.plantsrecognizer.Models;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class JsonModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title = "";
    private String description = "";
    private String source = "";
    private String image_height = "";
    private String image_width = "";
    private String page_image = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource(){
        return source;
    }

    public void setSource(String source){
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public String toString() {
        return title + ":\n" + description + "\n" + source;
    }

    //TODO: Remove unused blocks of code when finish
    /*
    public String getImageWidth() {
        return image_width;
    }

    public void setImageWidth(String image_width) {
        this.image_width = image_width;
    }

    public String getImageHeight() {
        return image_height;
    }

    public void setImageHeight(String image_height) {
        this.image_height = image_height;
    }

    public void setSource(String raw_url,String source){
        this.source = raw_url + source;
    }

    public void setPageImage(String page_image){
        this.page_image = page_image;
    }

    public String getPageImage(){
        return page_image;
    }

    public byte[] getBytes() {
        String result_string = title + ";" + description + ";" + source;
        return result_string.getBytes();
    }
    */
}
