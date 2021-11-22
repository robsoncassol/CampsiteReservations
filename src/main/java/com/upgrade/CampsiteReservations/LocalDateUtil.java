package com.upgrade.CampsiteReservations;

import com.upgrade.CampsiteReservations.reservations.model.Reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalDateUtil {

  public static List<LocalDate> getDaysBetweenDates(Reservation reservation) {
    return Stream.iterate(reservation.getArrivalDate(), date -> date.plusDays(1))
        .limit(ChronoUnit.DAYS.between(reservation.getArrivalDate(), reservation.getDepartureDate())).collect(Collectors.toList());
  }

}
