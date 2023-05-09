package com.gcp.demo;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.apache.http.HttpStatus.SC_OK;

import org.junit.Test;

public class CireneFunctionalTest {

  private static final String CIRENE_URL = "http://localhost:4000/cirene";

  private static final String APPLICATION_JSON = "application/json";

  private static final String TEXT_PLAIN = "text/plain";

  @Test
  public void whenCallingPlanetSizeThenReturn200AndSizeEquals4003() {
    given()
      .queryParam("radius", "35.7")
    .when()
      .get(CIRENE_URL + "/planet/size")
    .then()
      .assertThat()
        .statusCode(SC_OK)
      .assertThat()
        .contentType(TEXT_PLAIN)
      .assertThat()
        .body(equalTo("4003.93"));
  }

  @Test
  public void whenCallingPlanetSizeInfoThenReturn200AndFullResponse() {
    given()
      .queryParam("radius", "35.7")
    .when()
      .get(CIRENE_URL + "/planet/size/info")
    .then()
      .assertThat()
        .statusCode(SC_OK)
      .assertThat()
        .contentType(APPLICATION_JSON)
      .assertThat()
        .body("size", equalTo(4003.93f))
      .assertThat()
        .body("rating", equalTo("MEDIUM"))
      .assertThat()
        .body("info.hostname", is(notNullValue()))
      .assertThat()
        .body("info.hostaddress", is(notNullValue()));    
  }

  @Test
  public void whenCallingHealthCheckThenReturn200AndStatusEqualsUp() {
    given()
    .when()
      .get(CIRENE_URL + "/health")
    .then()
      .assertThat()
        .statusCode(SC_OK)
      .assertThat()
        .contentType(APPLICATION_JSON)
      .assertThat()
        .body("status", equalTo("up"));
  }

}
