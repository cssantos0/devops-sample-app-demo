package com.gcp.cirene.rest;

import org.springframework.web.bind.annotation.RestController;

import com.gcp.cirene.rest.model.Request;
import com.gcp.cirene.service.CireneService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("cirene")
public class CireneController {

    @Autowired
    private CireneService cireneService;

    @RequestMapping(value = "/planet/size", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getPlanetSize(final Request request) {
        final Float radius = this.cireneService.calculatePlanetSize(request.getRadius());
        return ResponseEntity
                .ok()
                .body(radius.toString());
    }

    @RequestMapping(value = "/planet/size/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPlanetSizeWithInfo(final Request request) {
        final String payload = this.cireneService.calculatePlanetSizeWithInfo(request.getRadius());
        return ResponseEntity
                .ok()
                .body(payload);
    }

    @RequestMapping(value = "/health", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> health() {
        return ResponseEntity
                .ok()
                .body(this.cireneService.createHealthCheckOutout());
    }

}