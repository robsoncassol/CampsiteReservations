package com.upgrade.CampsiteReservations.reservations.controller;

import com.jupitertools.springtestredis.RedisTestContainer;
import com.upgrade.CampsiteReservations.exceptions.ExceptionDTO;
import com.upgrade.CampsiteReservations.reservations.dto.AvailableDateDTO;
import com.upgrade.CampsiteReservations.reservations.dto.ReservationDTO;
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

@SpringBootTest(webEnvironment = RANDOM_PORT)
@RedisTestContainer
class ReservationControllerTest {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    private String getBaseUrl() {
        return "http://localhost:" + port + "/reservations";
    }

    @Test
    void testListAvailableDates() {
        ResponseEntity<List<AvailableDateDTO>> responseEntity =
                restTemplate.exchange(
                        getBaseUrl()+"?from=2021-10-20&until=2021-10-30",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().size()).isEqualTo(11);
    }

    @Test
    void testListAvailableDatesShouldReturn400WhenMissingParameter(){
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
    void shouldReturnBadRequestWhenMissingParameterName() {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setArrivalDate(LocalDate.now().plusDays(10));
        reservationDTO.setDepartureDate(LocalDate.now().plusDays(20));
        reservationDTO.setEmail("email@email.com");
        ResponseEntity<ExceptionDTO> responseEntity = testRequiredParameters(reservationDTO);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getErrorDetails()).contains("Name can't be empty");
    }

    @Test
    void shouldReturnBadRequestWhenMissingParameterEmail() {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setArrivalDate(LocalDate.now().plusDays(10));
        reservationDTO.setDepartureDate(LocalDate.now().plusDays(20));
        reservationDTO.setName("name");
        ResponseEntity<ExceptionDTO> responseEntity = testRequiredParameters(reservationDTO);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getErrorDetails()).contains("Email can't be empty");
    }


    @Test
    void shouldReturnBadRequestWhenMissingParameterDepartureDate() {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setArrivalDate(LocalDate.now().plusDays(10));
        reservationDTO.setName("name");
        reservationDTO.setEmail("email@email.com");
        ResponseEntity<ExceptionDTO> responseEntity = testRequiredParameters(reservationDTO);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getErrorDetails()).contains("Departure date can't be null");
    }

    @Test
    void shouldReturnBadRequestWhenMissingParameterArrivalDate() {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setDepartureDate(LocalDate.now().plusDays(20));
        reservationDTO.setName("name");
        reservationDTO.setEmail("email@email.com");
        ResponseEntity<ExceptionDTO> responseEntity = testRequiredParameters(reservationDTO);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getErrorDetails()).contains("Arrival date can't be null");
    }

    @Test
    void shouldReturnBadRequestWhenInvalidEmailFormat() {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setArrivalDate(LocalDate.now().plusDays(10));
        reservationDTO.setDepartureDate(LocalDate.now().plusDays(20));
        reservationDTO.setName("name");
        reservationDTO.setEmail("email.com");
        ResponseEntity<ExceptionDTO> responseEntity = testRequiredParameters(reservationDTO);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getErrorDetails()).contains("must be a well-formed email address");
    }


    private ResponseEntity<ExceptionDTO> testRequiredParameters(ReservationDTO reservationDTO) {
        HttpEntity<ReservationDTO> request = new HttpEntity<>(reservationDTO);
        ResponseEntity<ExceptionDTO> responseEntity = restTemplate.exchange(
            getBaseUrl(),
            HttpMethod.POST,
            request,
            ExceptionDTO.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getError()).isEqualTo("Request validation error");
        return responseEntity;
    }


    @Test
    void shouldSaveNewReservation() {
        ResponseEntity<ReservationDTO> responseEntity = createNewReservation(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getId()).isNotNull();
    }

    private ResponseEntity<ReservationDTO> createNewReservation(LocalDate from, LocalDate until) {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setArrivalDate(from);
        reservationDTO.setDepartureDate(until);
        reservationDTO.setName("name");
        reservationDTO.setEmail("email@email.com");
        HttpEntity<ReservationDTO> request = new HttpEntity<>(reservationDTO);
        return restTemplate.exchange(
            getBaseUrl(),
            HttpMethod.POST,
            request,
            ReservationDTO.class
        );
    }

    @Test
    void updateReservation() {
        ResponseEntity<ReservationDTO> response = createNewReservation(LocalDate.now().plusDays(12), LocalDate.now().plusDays(14));
        ReservationDTO reservationDTO = response.getBody();
        assertThat(reservationDTO).isNotNull();
        LocalDate arrivalDate = LocalDate.now().plusDays(13);
        reservationDTO.setArrivalDate(arrivalDate);
        HttpEntity<ReservationDTO> request = new HttpEntity<>(reservationDTO);
        ResponseEntity<ReservationDTO> responseEntity = restTemplate.exchange(
            getBaseUrl() + "/" + reservationDTO.getId(),
            HttpMethod.PUT,
            request,
            ReservationDTO.class
        );
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getArrivalDate()).isEqualTo(arrivalDate);
    }

    @Test
    void cancelReservation() {
        ResponseEntity<ReservationDTO> response = createNewReservation(LocalDate.now().plusDays(10), LocalDate.now().plusDays(12));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        restTemplate.delete(getBaseUrl() + "/" + response.getBody().getId());

        ResponseEntity<ReservationDTO> exchange = restTemplate.exchange(
            getBaseUrl() + "/" + response.getBody().getId(),
            HttpMethod.GET,
            null,
            ReservationDTO.class
        );

        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}