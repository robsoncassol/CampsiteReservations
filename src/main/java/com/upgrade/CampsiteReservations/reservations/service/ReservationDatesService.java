package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.LocalDateUtil;
import com.upgrade.CampsiteReservations.reservations.exceptions.PeriodIsNoLongerAvailableException;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.model.ReservationDate;
import com.upgrade.CampsiteReservations.reservations.repository.ReservationDaysRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReservationDatesService {

  private ReservationDaysRepository reservationDaysRepository;


  public ReservationDatesService(ReservationDaysRepository reservationDaysRepository) {
    this.reservationDaysRepository = reservationDaysRepository;
  }

  /**
   * set the query per month to increase the cache efficiency.
   *
   * @return List of days that has a reservation
   */
  private List<LocalDate> getBusyDaysByMonth(int year, Month month) {
    LocalDate monthStart = LocalDate.of(year, month, 1);
    LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
    return reservationDaysRepository.findAllByDayBetween(monthStart, monthEnd).stream()
        .filter(ReservationDate::isBusy)
        .map(ReservationDate::getDay)
        .collect(Collectors.toList());
  }

  public List<LocalDate> getBusyDays(LocalDate from, LocalDate to) {
    return Stream.iterate(from, date -> date.plusMonths(1))
        .limit(ChronoUnit.MONTHS.between(from, to.plusMonths(1)))
        .flatMap(month -> getBusyDaysByMonth(month.getYear(), month.getMonth()).stream())
        .collect(Collectors.toList());
  }

  public void periodIsAvailable(Reservation reservation) {
    periodIsAvailable(reservation, new HashSet<>());
  }


  public void periodIsAvailable(Reservation newReservation, Set<ReservationDate> currentReservationDays) {
    List<LocalDate> busyDays = getBusyDays(newReservation.getArrivalDate(), newReservation.getDepartureDate());
    busyDays.removeAll(currentReservationDays.stream().map(r -> r.getDay()).collect(Collectors.toList()));
    LocalDateUtil.getDaysBetweenDates(newReservation)
        .stream()
        .filter(busyDays::contains)
        .findAny()
        .ifPresent(s -> {
          throw new PeriodIsNoLongerAvailableException(newReservation.getArrivalDate(), newReservation.getDepartureDate());
        });
  }
}
