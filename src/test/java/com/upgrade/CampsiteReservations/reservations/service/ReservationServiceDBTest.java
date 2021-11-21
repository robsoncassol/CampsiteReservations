package com.upgrade.CampsiteReservations.reservations.service;

import com.jupitertools.springtestredis.RedisTestContainer;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@RedisTestContainer
class ReservationServiceDBTest {

  @Autowired
  private ReservationService reservationService;

  @Autowired
  private CampsiteAvailabilityService campsiteAvailabilityService;


  @Test
  void testCheckoutDayShouldBeConsideredAvailable() {
    LocalDate checkoutCheckInDay = LocalDate.of(2021, 11, 18);
    saveReservation(LocalDate.of(2021, 11, 15), checkoutCheckInDay);
    saveReservation(checkoutCheckInDay, LocalDate.of(2021, 11, 21));
    LocalDate novemberFirst = LocalDate.of(2021, 11, 1);
    List<LocalDate> busyDaysByMonth = campsiteAvailabilityService.getBusyDays(novemberFirst,novemberFirst.withDayOfMonth(novemberFirst.lengthOfMonth()));
    Assertions.assertEquals(6,busyDaysByMonth.size());
  }

  @Test
  void testAfterReservationUpdateTheNumberOfBusyDaysShouldReflectIt() {
    LocalDate arrivalDate = LocalDate.of(2021, 10, 10);
    LocalDate departureDate = LocalDate.of(2021, 10, 13);
    Reservation reservation = saveReservation(arrivalDate, departureDate);
    reservation.setDepartureDate(LocalDate.of(2021,10,12));
    reservationService.updateReservation(reservation.getId(),reservation);
    LocalDate octoberFirst = LocalDate.of(2021, 10, 1);
    List<LocalDate> busyDaysByMonth = campsiteAvailabilityService.getBusyDays(octoberFirst,octoberFirst.withDayOfMonth(octoberFirst.lengthOfMonth()));
    Assertions.assertEquals(2,busyDaysByMonth.size());
  }

  private Reservation saveReservation(LocalDate arrivalDate, LocalDate departureDate) {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(arrivalDate);
    reservation.setDepartureDate(departureDate);
    reservation.setEmail("robsoncassol@gmail.com");
    reservation.setName("robson cassol");
    return reservationService.bookCampsite(reservation);
  }

}