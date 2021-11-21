package com.upgrade.CampsiteReservations.reservations.service;

import com.jupitertools.springtestredis.RedisTestContainer;
import com.upgrade.CampsiteReservations.reservations.dto.AvailableDateDTO;
import com.upgrade.CampsiteReservations.reservations.exceptions.PeriodIsNoLongerAvailableException;
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

  @Test
  void testSaveTwoReservationsInTheSamePeriod() {
    LocalDate arrival = LocalDate.of(2021, 9, 18);
    LocalDate departure = LocalDate.of(2021, 9, 19);
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(arrival);
    reservation.setDepartureDate(departure);
    reservation.setEmail("johnwick@gmail.com");
    reservation.setName("john wick");
    reservationService.bookCampsite(reservation);
    PeriodIsNoLongerAvailableException exception = Assertions.assertThrows(PeriodIsNoLongerAvailableException.class,
        () -> reservationService.bookCampsite(reservation));
    Assertions.assertEquals("Selected period is no longer available [2021-09-18 - 2021-09-19]",exception.getErrorDetails());

  }

  @Test
  void testCheckoutDayShouldBeConsideredAvailable() {
    LocalDate checkoutCheckInDay = LocalDate.of(2021, 11, 18);
    saveReservation(LocalDate.of(2021, 11, 15), checkoutCheckInDay);
    saveReservation(checkoutCheckInDay, LocalDate.of(2021, 11, 21));
    LocalDate novemberFirst = LocalDate.of(2021, 11, 1);
    List<AvailableDateDTO> availableDates = reservationService.getAvailableDates(novemberFirst, novemberFirst.withDayOfMonth(novemberFirst.lengthOfMonth()));
    //assert busy days
    Assertions.assertEquals(6,availableDates.stream().filter(a -> !a.isAvailable()).count());
  }

  @Test
  void testAfterReservationUpdateTheNumberOfBusyDaysShouldReflectIt() {
    LocalDate arrivalDate = LocalDate.of(2021, 10, 10);
    LocalDate departureDate = LocalDate.of(2021, 10, 13);
    Reservation reservation = saveReservation(arrivalDate, departureDate);
    reservation.setDepartureDate(LocalDate.of(2021,10,12));
    reservationService.updateReservation(reservation.getId(),reservation);
    LocalDate octoberFirst = LocalDate.of(2021, 10, 1);
    List<AvailableDateDTO> availableDates = reservationService.getAvailableDates(octoberFirst,octoberFirst.withDayOfMonth(octoberFirst.lengthOfMonth()));
    Assertions.assertEquals(2,availableDates.stream().filter(a -> !a.isAvailable()).count());
  }

  @Test
  void testCancelReservationShouldReleaseAllDays() {
    LocalDate checkoutCheckInDay = LocalDate.of(2021, 11, 18);
    Reservation reservation = saveReservation(LocalDate.of(2021, 11, 15), checkoutCheckInDay);

    reservationService.cancelReservation(reservation);

    LocalDate novemberFirst = LocalDate.of(2021, 11, 1);
    List<AvailableDateDTO> availableDates = reservationService.getAvailableDates(novemberFirst, novemberFirst.withDayOfMonth(novemberFirst.lengthOfMonth()));
    //assert busy days
    Assertions.assertEquals(0,availableDates.stream().filter(a -> !a.isAvailable()).count());
  }

  private Reservation saveReservation(LocalDate arrivalDate, LocalDate departureDate) {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(arrivalDate);
    reservation.setDepartureDate(departureDate);
    reservation.setEmail("johnwick@gmail.com");
    reservation.setName("john wick");
    return reservationService.bookCampsite(reservation);
  }

}