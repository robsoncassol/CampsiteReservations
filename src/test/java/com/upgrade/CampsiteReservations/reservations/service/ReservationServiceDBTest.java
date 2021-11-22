package com.upgrade.CampsiteReservations.reservations.service;

import com.jupitertools.springtestredis.RedisTestContainer;
import com.upgrade.CampsiteReservations.reservations.dto.AvailableDateDTO;
import com.upgrade.CampsiteReservations.reservations.exceptions.PeriodIsNoLongerAvailableException;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.model.ReservationDate;
import com.upgrade.CampsiteReservations.reservations.repository.ReservationRepository;
import com.upgrade.CampsiteReservations.reservations.service.cache.ReservationDaysCacheHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@RedisTestContainer
class ReservationServiceDBTest {

  @Autowired
  private ReservationService reservationService;

  @Autowired
  private ReservationDaysCacheHandler reservationDaysCacheHandler;

  @Autowired
  private ReservationRepository reservationRepository;

  @AfterEach
  void cleanUp(){
    reservationRepository.deleteAll();
    reservationDaysCacheHandler.cacheEvict();
  }

  @Test
  void testShouldThrowExceptionWhenSaveTwoReservationsInTheSamePeriod() {
    LocalDate today = LocalDate.now();
    LocalDate arrival = today.plusDays(2);
    LocalDate departure = today.plusDays(4);
    Reservation reservation1 = new Reservation();
    reservation1.setArrivalDate(arrival);
    reservation1.setDepartureDate(departure);
    reservation1.setEmail("johnwick@gmail.com");
    reservation1.setName("john wick");
    Reservation reservation2 = new Reservation();
    reservation2.setArrivalDate(arrival);
    reservation2.setDepartureDate(departure);
    reservation2.setEmail("jackwick@gmail.com");
    reservation2.setName("jack wick");
    reservationService.bookCampsite(reservation1);
    PeriodIsNoLongerAvailableException exception = Assertions.assertThrows(PeriodIsNoLongerAvailableException.class,
        () -> reservationService.bookCampsite(reservation2));
    Assertions.assertEquals(String.format("Selected period is no longer available [%s - %s]",arrival,departure),exception.getErrorDetails());
  }

  @Test
  void shouldThrowExceptionWhenSaveTwoReservationInTheSamePeriod(){
    LocalDate today = LocalDate.now();
    LocalDate arrival = today.plusDays(2);
    LocalDate departure = today.plusDays(3);
    Reservation reservation1 = new Reservation();
    reservation1.setArrivalDate(arrival);
    reservation1.setDepartureDate(departure);
    reservation1.setEmail("johnwick@gmail.com");
    reservation1.setName("john wick");
    reservation1.addReservationDates(List.of(new ReservationDate(arrival)));
    Reservation reservation2 = new Reservation();
    reservation2.setArrivalDate(arrival);
    reservation2.setDepartureDate(departure);
    reservation2.setEmail("jackwick@gmail.com");
    reservation2.setName("jack wick");
    reservation2.addReservationDates(List.of(new ReservationDate(arrival)));
    reservationRepository.save(reservation1);
    DataIntegrityViolationException exception = Assertions.assertThrows(DataIntegrityViolationException.class,
        () -> reservationRepository.save(reservation2));
    org.assertj.core.api.Assertions.assertThat(exception).hasMessageContaining("RESERVATION_DATE_UN");
  }

  @Test
  void testCheckoutDayShouldBeConsideredAvailable() {
    LocalDate today = LocalDate.now();
    LocalDate checkoutCheckInDay = today.plusDays(7);
    saveReservation(today.plusDays(5), checkoutCheckInDay);
    saveReservation(checkoutCheckInDay, today.plusDays(10));
    List<AvailableDateDTO> availableDates = reservationService.getAvailableDates(today, today.plusMonths(1));
    //assert busy days
    Assertions.assertEquals(5,availableDates.stream().filter(a -> !a.isAvailable()).count());
  }

  @Test
  void testAfterReservationUpdateTheNumberOfBusyDaysShouldReflectIt() {
    LocalDate today = LocalDate.now();
    Reservation reservation = saveReservation(today.plusDays(10), today.plusDays(13));

    List<AvailableDateDTO> availableDates = reservationService.getAvailableDates(today,today.plusMonths(1));
    Assertions.assertEquals(3,availableDates.stream().filter(a -> !a.isAvailable()).count());

    reservation.setDepartureDate(today.plusDays(12));
    reservationService.updateReservation(reservation.getId(),reservation);
    availableDates = reservationService.getAvailableDates(today,today.plusMonths(1));
    Assertions.assertEquals(2,availableDates.stream().filter(a -> !a.isAvailable()).count());
  }

  @Test
  void testUpdateReservationToAnInvalidPeriod() {
    LocalDate today = LocalDate.now();
    saveReservation(today.plusDays(10), today.plusDays(13));
    Reservation reservation = saveReservation(today.plusDays(14), today.plusDays(15));
    reservation.setArrivalDate(today.plusDays(12));
    PeriodIsNoLongerAvailableException exception = Assertions.assertThrows(PeriodIsNoLongerAvailableException.class,
        () -> reservationService.updateReservation(reservation.getId(),reservation));
  }

  @Test
  void testCancelReservationShouldReleaseAllDays() {
    LocalDate today = LocalDate.now();
    Reservation reservation = saveReservation(today.plusDays(1), today.plusDays(2));

    reservationService.cancelReservation(reservation);

    List<AvailableDateDTO> availableDates = reservationService.getAvailableDates(today, today.plusMonths(1));
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