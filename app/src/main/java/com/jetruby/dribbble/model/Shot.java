package com.jetruby.dribbble.model;

import java.io.Serializable;

public class Shot implements Serializable {
    private int id;
    private String title;
    private String date;
    private String description;

    public String getAnimated() {
        return animated;
    }

    public void setAnimated(String animated) {
        this.animated = animated;
    }

    private String animated;

    private String hidpi, normal, teaser;


    public Shot() {
    }

    public Shot(int id, String title, String date, String description, String hidpi, String normal, String teaser) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.description = description;
        this.hidpi = hidpi;
        this.normal = normal;
        this.teaser = teaser;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getHidpi() {
        return hidpi;
    }

    public void setHidpi(String hidpi) {
        this.hidpi = hidpi;
    }


    public String getNormal() {
        return normal;
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }
}