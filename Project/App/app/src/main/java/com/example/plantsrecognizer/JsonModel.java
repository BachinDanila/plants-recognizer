package com.example.plantsrecognizer;

import java.io.Serializable;

public class JsonModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title = "";
    private String description = "";
    private String source = "";
    private String imageheight = "";
    private String imagewidth = "";
    private String pageimage = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPageImage(){
        return pageimage;
    }

    public void setPageImage(String pageimage){
        this.pageimage = pageimage;
    }

    public String getSource(){
        return source;
    }

    public String getImageWidth() {
        return imagewidth;
    }

    public void setImageWidth(String imagewidth) {
        this.imagewidth = imagewidth;
    }

    public String getImageHeight() {
        return imageheight;
    }

    public void setImageHeight(String imageheight) {
        this.imageheight = imageheight;
    }

    public void setSource(String source){
        this.source = source;
    }

    public void setSource(String raw_url,String source){
        this.source = raw_url + source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getBytes() {
        String result_string = title + ";" + description + ";" + source;
        return result_string.getBytes();
    }

}
