package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.reservations.exceptions.InconsistentReservationDatesException;
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

    Assertions.assertTrue(new ReservationValidator(3).validated(reservation));
  }

  @Test
  void shouldThrowExceptionWhenDepartureDateEqualsArrivalDate() {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.now().plusDays(10));
    reservation.setDepartureDate(LocalDate.now().plusDays(10));

    InconsistentReservationDatesException exception = Assertions.assertThrows(InconsistentReservationDatesException.class,
        () -> new ReservationValidator(3).validated(reservation));

    Assertions.assertEquals("The arrival date is the same as the departure date", exception.getErrorDetails());
  }

  @Test
  void shouldThrowExceptionWhenDepartureDateIsBeforeArrivalDate() {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.now().plusDays(10));
    reservation.setDepartureDate(LocalDate.now().plusDays(5));

    InconsistentReservationDatesException exception = Assertions.assertThrows(InconsistentReservationDatesException.class,
        () -> new ReservationValidator(3).validated(reservation));

    Assertions.assertEquals("Arrival date is after departure date", exception.getErrorDetails());
  }

  @Test
  void shouldThrowExceptionWhenTheReservationPeriodIsBiggerThanTheMaximumAllowed() {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.now().plusDays(10));
    reservation.setDepartureDate(LocalDate.now().plusDays(20));

    InconsistentReservationDatesException exception = Assertions.assertThrows(InconsistentReservationDatesException.class,
        () -> new ReservationValidator(3).validated(reservation));

    Assertions.assertEquals("The booking period cannot be longer than 3 days", exception.getErrorDetails());
  }

}