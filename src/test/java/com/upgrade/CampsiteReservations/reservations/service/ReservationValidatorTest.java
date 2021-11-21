package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.reservations.exceptions.InvalidPeriodException;
import com.upgrade.CampsiteReservations.reservations.exceptions.InvalidSearchPeriodException;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class ReservationValidatorTest {

  @Test
  void testValidPeriod() {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.now().plusDays(10));
    reservation.setDepartureDate(LocalDate.now().plusDays(12));

    Assertions.assertTrue(new ReservationValidator(3,365).validated(reservation));
  }

  @Test
  void shouldThrowExceptionWhenDepartureDateEqualsArrivalDate() {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.now().plusDays(10));
    reservation.setDepartureDate(LocalDate.now().plusDays(10));

    InvalidPeriodException exception = Assertions.assertThrows(InvalidPeriodException.class,
        () -> new ReservationValidator(3,365).validated(reservation));

    Assertions.assertEquals("The arrival date is the same as the departure date", exception.getErrorDetails());
  }

  @Test
  void shouldThrowExceptionWhenDepartureDateIsBeforeArrivalDate() {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.now().plusDays(10));
    reservation.setDepartureDate(LocalDate.now().plusDays(5));

    InvalidPeriodException exception = Assertions.assertThrows(InvalidPeriodException.class,
        () -> new ReservationValidator(3,365).validated(reservation));

    Assertions.assertEquals("Arrival date is after departure date", exception.getErrorDetails());
  }

  @Test
  void shouldThrowExceptionWhenTheReservationPeriodIsBiggerThanTheMaximumAllowed() {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.now().plusDays(10));
    reservation.setDepartureDate(LocalDate.now().plusDays(20));

    InvalidPeriodException exception = Assertions.assertThrows(InvalidPeriodException.class,
        () -> new ReservationValidator(3,365).validated(reservation));

    Assertions.assertEquals("The booking period cannot be longer than 3 days", exception.getErrorDetails());
  }


  @Test
  void testValidSearchPeriod() {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.now().plusDays(10));
    reservation.setDepartureDate(LocalDate.now().plusDays(12));

    Assertions.assertTrue(new ReservationValidator(3,365).validated(reservation));
  }

  @Test
  void shouldThrowExceptionWhenFromDateEqualsUtilDate() {
    LocalDate from = LocalDate.now().plusDays(10);
    LocalDate until = LocalDate.now().plusDays(10);

    InvalidSearchPeriodException exception = Assertions.assertThrows(InvalidSearchPeriodException.class,
        () -> new ReservationValidator(3,365).validateAvailableDatesRange(from,until));

    Assertions.assertEquals("The initial date is the same as the end date", exception.getErrorDetails());
  }

  @Test
  void shouldThrowExceptionWhenFromIsAfterUtil() {
    LocalDate from = LocalDate.now().plusDays(10);
    LocalDate until = LocalDate.now().plusDays(5);

    InvalidSearchPeriodException exception = Assertions.assertThrows(InvalidSearchPeriodException.class,
        () -> new ReservationValidator(3,365).validateAvailableDatesRange(from,until));

    Assertions.assertEquals("The final date is after the initial date", exception.getErrorDetails());
  }

  @Test
  void shouldThrowExceptionWhenTheSearchPeriodIsBiggerThanTheMaximumAllowed() {
    LocalDate from = LocalDate.now().plusDays(10);
    LocalDate until = LocalDate.now().plusDays(400);

    InvalidSearchPeriodException exception = Assertions.assertThrows(InvalidSearchPeriodException.class,
        () -> new ReservationValidator(3,365).validateAvailableDatesRange(from,until));

    Assertions.assertEquals("The searching period cannot be longer than 365 days", exception.getErrorDetails());
  }

}