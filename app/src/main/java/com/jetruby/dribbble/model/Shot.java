package com.jetruby.dribbble.model;

import java.io.Serializable;

public class Shot implements Serializable {
    private int id;
    private String title;
    private String date;
    private String description;
    private int likes_count;
    private int views_count;
    private int comments_count;
    private String comments_url;
    private String small, medium, large;


    public Shot() {
    }

    public Shot(int id, String title, String date, String description, int likes_count, int views_count, int comments_count, String comments_url, String small, String medium, String large) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.description = description;
        this.likes_count = likes_count;
        this.views_count = views_count;
        this.comments_count = comments_count;
        this.comments_url = comments_url;
        this.small = small;
        this.medium = medium;
        this.large = large;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getViews_count() {
        return views_count;
    }

    public void setViews_count(int views_count) {
        this.views_count = views_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getComments_url() {
        return comments_url;
    }

    public void setComments_url(String comments_url) {
        this.comments_url = comments_url;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }
}