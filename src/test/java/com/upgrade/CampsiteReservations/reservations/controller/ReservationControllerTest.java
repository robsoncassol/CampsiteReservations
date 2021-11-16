package com.upgrade.CampsiteReservations.reservations.controller;

import com.upgrade.CampsiteReservations.config.TestRedisConfiguration;
import com.upgrade.CampsiteReservations.reservations.dto.ReservationDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {TestRedisConfiguration.class})
class ReservationControllerTest {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    private String getBaseUrl() {
        return "http://localhost:" + port + "/reservation";
    }

    @Test
    void testListAvailableDates() {
        ResponseEntity<List<LocalDate>> responseEntity =
                restTemplate.exchange(
                        getBaseUrl()+"?from=2021-10-20&until=2021-10-30",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().size()).isEqualTo(11);
    }

    @Test
    void shouldReturn400WhenMissingParameter(){
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(
                        getBaseUrl()+"?from=2021-10-10",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    void shouldReturnBadRequestf() {
        HttpEntity<ReservationDTO> request = new HttpEntity<>(new ReservationDTO());
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                request,
                String.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateReservation() {
    }

    @Test
    void cancelReservation() {
    }
}