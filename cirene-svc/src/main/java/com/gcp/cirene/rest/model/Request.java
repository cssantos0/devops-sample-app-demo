package com.gcp.cirene.rest.model;

import java.io.Serializable;

public class Request implements Serializable {

    private float radius;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public String toString() {
        return "Request [radius=" + radius + "]";
    }

}
