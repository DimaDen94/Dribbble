package com.jetruby.dribbble.model;

/**
 * Created by Dmitry on 17.01.2017.
 */
public class Images {
    private String hidpi, normal, teaser;

    public String getHidpi() {
        return hidpi;
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

    public void setHidpi(String hidpi) {
        this.hidpi = hidpi;

    }
}
