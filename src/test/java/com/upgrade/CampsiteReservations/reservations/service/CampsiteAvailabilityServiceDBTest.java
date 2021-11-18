package com.upgrade.CampsiteReservations.reservations.service;

import com.jupitertools.springtestredis.RedisTestContainer;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@SpringBootTest
@RedisTestContainer
class CampsiteAvailabilityServiceDBTest {

  @Autowired
  private ReservationService reservationService;

  @Autowired
  private CampsiteAvailabilityService campsiteAvailabilityService;


  @Test
  void shouldSaveReservation() {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.of(2021,10,15));
    reservation.setDepartureDate(LocalDate.of(2021,10,20));
    reservation.setEmail("robsoncassol@gmail.com");
    reservation.setName("robson cassol");
    Reservation saved = reservationService.bookCampsite(reservation);
    Assertions.assertNotNull(saved.getId());
  }

  @Test
  void shouldUpdateReservation() {
    Reservation reservation = saveReservation();
    reservation.setDepartureDate(LocalDate.of(2021,10,19));
    reservationService.updateReservation(reservation.getId(),reservation);
    List<LocalDate> busyDaysByMonth = campsiteAvailabilityService.getBusyDaysByMonth(2021, Month.OCTOBER);
    Assertions.assertEquals(4,busyDaysByMonth.size());
  }

  private Reservation saveReservation() {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.of(2021,10,15));
    reservation.setDepartureDate(LocalDate.of(2021,10,20));
    reservation.setEmail("robsoncassol@gmail.com");
    reservation.setName("robson cassol");
    return reservationService.bookCampsite(reservation);
  }

}