package com.gcp.cirene.service;

public interface CireneService {

    public Float calculatePlanetSize(final float radius);

    public String calculatePlanetSizeWithInfo(final float radius);

    public String createHealthCheckOutout();
}