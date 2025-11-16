package com.example.cinedex_v2.Data.Models;

public class News {
    public String title;
    public String summary;
    public String imageUrl;
    public String meta;

    public News(String title, String summary, String imageUrl, String meta) {
        this.title = title;
        this.summary = summary;
        this.imageUrl = imageUrl;
        this.meta = meta;
    }
}
