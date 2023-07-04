package com.gcp.cirene.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CireneServiceImpl implements CireneService {

    @Value("${ENABLE_INFO:false}")
    private Boolean ENABLE_INFO;

    @Value("${ENABLE_RATING:false}")
    private Boolean ENABLE_RATING;

    @Override
    public Float calculatePlanetSize(final float radius) {
        return this.calculatePlanetArea(radius);
    }

    public String calculatePlanetSizeWithInfo(final float radius) {
        try {
            final Float planetArea = this.calculatePlanetArea(radius);
            final String rating = this.generateRating(planetArea);

            final StringBuilder builder = new StringBuilder();
            builder.append("{");
                //test      
                builder.append("\"size\": ");
                builder.append(planetArea);

                if (ENABLE_RATING) {
                    builder.append(",");
                    builder.append("\"rating\": ");
                    builder.append("\"");
                    builder.append(rating);
                    builder.append("\"");
                }

                if (ENABLE_INFO) {
                    builder.append(",");
                    builder.append("\"info\": { ");

                    builder.append("\"hostname\": ");
                    builder.append("\"");
                    builder.append(InetAddress.getLocalHost().getHostName());
                    builder.append("\",");

                    builder.append("\"hostaddress\": ");
                    builder.append("\"");
                    builder.append(InetAddress.getLocalHost().getHostAddress());
                    builder.append("\"");
                    builder.append("}");
                }

            builder.append("}");
            return builder.toString();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            final StringBuilder builder = new StringBuilder();
            builder.append("{\"status\":\"ERROR\"}");
            return builder.toString();
        }
    }

    public String createHealthCheckOutout() {
        //try {
            final StringBuilder builder = new StringBuilder();
            builder.append("{\"status\":\"up\"}");
            return builder.toString();
        //} catch (Exception ex) {
        //    ex.printStackTrace();
        //    return null;
        //}
    }

    private Float calculatePlanetArea(final float radius) {
        final double area = radius * radius * Math.PI;
        BigDecimal bd = new BigDecimal(Double.toString(area));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    private String generateRating (final Float area) {
        if (area < 1000) {
            return "SMALL";
        } else if (area > 1000 && area < 5000) {
            return "MEDIUM";
        } else {
            return "LARGE";
        }
    }

}