package com.example.newsapphw3;

import java.util.Date;

public class NewsItem {
    int id;
    String title;
    String text;
    String date;
    String image_url;
    String category_name;

    public NewsItem() {
    }

    public NewsItem(int id, String title, String text, String date, String image_url, String category_name) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.date = date;
        this.image_url = image_url;
        this.category_name = category_name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(int date_int) {
        this.date = date;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
