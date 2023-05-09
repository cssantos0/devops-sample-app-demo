package com.gcp.cirene.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class CireneServiceImplTests {

  @Test
   public void whenRadiusIsProvided_thenCalculatePlanetSizeSuccessfully() {
    CireneService cireneService = new CireneServiceImpl();
    float result = cireneService.calculatePlanetSize(35f);
    assertEquals(result, 3848.45f, 0.0f);
   }

   @Test
   public void whenAppIsUp_thenHealthCheckReturnsUpSuccessfully() {
    CireneService cireneService = new CireneServiceImpl();
    String expectedResponse = "{\"status\":\"up\"}";
    String result = cireneService.createHealthCheckOutout();
    assertEquals(expectedResponse, result);
   }
  
}
