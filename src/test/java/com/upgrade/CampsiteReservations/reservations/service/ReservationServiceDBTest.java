package com.upgrade.CampsiteReservations.reservations.service;

import com.jupitertools.springtestredis.RedisTestContainer;
import com.upgrade.CampsiteReservations.reservations.dto.AvailableDateDTO;
import com.upgrade.CampsiteReservations.reservations.exceptions.PeriodIsNoLongerAvailableException;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.repository.ReservationRepository;
import org.junit.jupiter.api.AfterEach;
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
  private ReservationRepository reservationRepository;

  @AfterEach
  void cleanUp(){
    reservationRepository.deleteAll();
  }

  @Test
  void testSaveTwoReservationsInTheSamePeriod() {
    LocalDate today = LocalDate.now();
    LocalDate arrival = today.plusDays(2);
    LocalDate departure = today.plusDays(4);
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(arrival);
    reservation.setDepartureDate(departure);
    reservation.setEmail("johnwick@gmail.com");
    reservation.setName("john wick");
    reservationService.bookCampsite(reservation);
    PeriodIsNoLongerAvailableException exception = Assertions.assertThrows(PeriodIsNoLongerAvailableException.class,
        () -> reservationService.bookCampsite(reservation));
    Assertions.assertEquals(String.format("Selected period is no longer available [%s - %s]",arrival,departure),exception.getErrorDetails());

  }

  @Test
  void testCheckoutDayShouldBeConsideredAvailable() {
    LocalDate today = LocalDate.now();
    LocalDate checkoutCheckInDay = today.plusDays(7);
    saveReservation(today.plusDays(5), checkoutCheckInDay);
    saveReservation(checkoutCheckInDay, today.plusDays(10));
    List<AvailableDateDTO> availableDates = reservationService.getAvailableDates(today.withDayOfMonth(1), today.plusMonths(1));
    //assert busy days
    Assertions.assertEquals(6,availableDates.stream().filter(a -> !a.isAvailable()).count());
  }

  @Test
  void testAfterReservationUpdateTheNumberOfBusyDaysShouldReflectIt() {
    LocalDate today = LocalDate.now();
    LocalDate arrival = today.plusDays(10);
    LocalDate departure = today.plusDays(13);
    Reservation reservation = saveReservation(arrival, departure);

    List<AvailableDateDTO> availableDates = reservationService.getAvailableDates(today.withDayOfMonth(1),today.plusMonths(1));
    Assertions.assertEquals(3,availableDates.stream().filter(a -> !a.isAvailable()).count());

    reservation.setDepartureDate(today.plusDays(12));
    reservationService.updateReservation(reservation.getId(),reservation);
    availableDates = reservationService.getAvailableDates(today.withDayOfMonth(1),today.plusMonths(1));
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